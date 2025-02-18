import React, { useState, useEffect } from 'react';
import { 
    Typography, 
    Paper, 
    Avatar, 
    Button,
    TextField,
    Box,
    Alert,
    IconButton
} from '@mui/material';
import { PhotoCamera, ArrowBack } from '@mui/icons-material';
import { authService } from '../services/authService';
import { useNavigate } from 'react-router-dom';
import ProfileImage from './ProfileImage';

const Settings = () => {
    const navigate = useNavigate();
    const [username, setUsername] = useState('');
    const [profileImage, setProfileImage] = useState(null);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    useEffect(() => {
        const fetchUserData = async () => {
            const token = localStorage.getItem('token');
            if (token) {
                try {
                    const base64Url = token.split('.')[1];
                    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
                    const payload = JSON.parse(window.atob(base64));
                    setUsername(payload.sub || 'User');
                    
                    // Fetch user data to get profile image URL
                    const response = await fetch(`http://localhost:8080/api/users/${payload.sub}`, {
                        headers: {
                            'Authorization': `Bearer ${token}`
                        }
                    });
                    
                    if (response.ok) {
                        const userData = await response.json();
                        if (userData.profileImage) {
                            const imageUrl = `http://localhost:8080${userData.profileImage}`;
                            console.log('Setting profile image URL:', imageUrl);
                            setProfileImage(imageUrl);
                        }
                    }
                } catch (err) {
                    console.error('Error fetching user data:', err);
                    setUsername('User');
                }
            }
        };

        fetchUserData();
    }, []);

    const handleImageUpload = async (event) => {
        const file = event.target.files[0];
        if (!file) return;

        // Add file size validation
        if (file.size > 5 * 1024 * 1024) { // 5MB limit
            setError('File size should be less than 5MB');
            return;
        }

        // Add file type validation
        if (!file.type.match('image.*')) {
            setError('Please upload an image file');
            return;
        }

        const formData = new FormData();
        formData.append('file', file);

        try {
            setError('');
            setSuccess('');
            
            const response = await fetch('http://localhost:8080/api/upload/profile-image', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                },
                body: formData
            });

            if (response.ok) {
                const data = await response.json();
                const imageUrl = `http://localhost:8080${data.imageUrl}`;
                console.log('New profile image URL:', imageUrl);
                setProfileImage(imageUrl);
                setSuccess('Profile picture updated successfully');
            } else {
                throw new Error('Failed to upload image');
            }
        } catch (err) {
            setError('Failed to update profile picture');
            console.error('Error uploading image:', err);
        }
    };

    const handleBackToDashboard = () => {
        navigate('/dashboard');
    };

    return (
        <div className="main-content" style={{ padding: '40px' }}>
            <Paper elevation={3} sx={{ p: 4, maxWidth: 600, mx: 'auto', position: 'relative' }}>
                <IconButton 
                    onClick={handleBackToDashboard}
                    sx={{ 
                        position: 'absolute',
                        left: 16,
                        top: 16,
                        color: '#6f42c1'
                    }}
                >
                    <ArrowBack />
                </IconButton>

                <Typography variant="h5" gutterBottom sx={{ textAlign: 'center', mt: 2 }}>
                    Profile Settings
                </Typography>

                {error && (
                    <Alert severity="error" sx={{ mb: 2 }}>
                        {error}
                    </Alert>
                )}
                {success && (
                    <Alert severity="success" sx={{ mb: 2 }}>
                        {success}
                    </Alert>
                )}

                <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', mb: 4 }}>
                    <ProfileImage
                        imageUrl={profileImage}
                        size={150}
                        sx={{ mb: 2 }}
                    />
                    <Button
                        variant="contained"
                        component="label"
                        startIcon={<PhotoCamera />}
                        sx={{ mt: 2 }}
                    >
                        Update Profile Picture
                        <input
                            type="file"
                            hidden
                            accept="image/*"
                            onChange={handleImageUpload}
                        />
                    </Button>
                </Box>

                <Box sx={{ mb: 3 }}>
                    <Typography variant="subtitle1" gutterBottom>
                        Username
                    </Typography>
                    <TextField
                        fullWidth
                        value={username}
                        disabled
                        variant="outlined"
                    />
                    <Typography variant="caption" color="textSecondary">
                        Username cannot be changed
                    </Typography>
                </Box>
            </Paper>
        </div>
    );
};

export default Settings; 