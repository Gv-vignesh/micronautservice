import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../services/authService';
import { 
    Typography, 
    IconButton, 
    InputBase,
    Badge,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    Avatar
} from '@mui/material';
import {
    Home as HomeIcon,
    Chat as ChatIcon,
    Settings as SettingIcon,
    Help as HelpIcon,
    Search as SearchIcon,
    Notifications as NotificationIcon,
    Menu as MenuIcon,
    Favorite as FavoriteIcon
} from '@mui/icons-material';
import '../styles/dashboard.css';
import ProfileImage from './ProfileImage';

const Dashboard = () => {
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const [username, setUsername] = useState('');
    const [openLogoutDialog, setOpenLogoutDialog] = useState(false);
    const [profileImage, setProfileImage] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        const fetchGreeting = async () => {
            try {
                const response = await authService.getGreeting();
                setMessage(response.data);
            } catch (err) {
                setError(err.message);
                if (err.response?.status === 401) {
                    navigate('/login');
                }
            }
        };

        const fetchUserData = async () => {
            const token = localStorage.getItem('token');
            if (!token) {
                navigate('/login');
                return;
            }
            
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
                        setProfileImage(`http://localhost:8080${userData.profileImage}`);
                    }
                }
            } catch (err) {
                console.error('Error fetching user data:', err);
                setUsername('User');
            }
        };

        fetchUserData();
        fetchGreeting();
    }, [navigate]);

    const handleLogoutClick = () => {
        setOpenLogoutDialog(true);
    };

    const handleLogoutConfirm = () => {
        authService.logout();
        navigate('/login');
    };

    const handleLogoutCancel = () => {
        setOpenLogoutDialog(false);
    };

    const handleSettingsClick = () => {
        navigate('/settings');
    };

    return (
        <div className="dashboard-layout">
            <div className="sidebar">
                <div className="profile-section">
                    <ProfileImage
                        imageUrl={profileImage}
                        sx={{ 
                            mb: 2,
                            border: '4px solid #ffffff40',
                            boxShadow: '0 4px 8px rgba(0,0,0,0.1)'
                        }}
                    />
                    <Typography variant="h6">{username}</Typography>
                </div>
                
                <div className="menu-item active">
                    <HomeIcon sx={{ mr: 2 }} />
                    <span>Dashboard</span>
                </div>
                <div className="menu-item">
                    <ChatIcon sx={{ mr: 2 }} />
                    <span>Chat</span>
                </div>
                <div className="menu-item" onClick={handleSettingsClick}>
                    <SettingIcon sx={{ mr: 2 }} />
                    <span>Setting</span>
                </div>
                <div className="menu-item">
                    <HelpIcon sx={{ mr: 2 }} />
                    <span>Help</span>
                </div>
            </div>

            <div className="main-content">
                <div className="header">
                    <div className="search-bar">
                        <SearchIcon sx={{ position: 'absolute', left: 15, top: 12, color: '#999' }} />
                        <InputBase
                            placeholder="Search Here"
                            className="search-input"
                            sx={{ pl: 5 }}
                        />
                    </div>
                    <div className="header-actions">
                        <IconButton>
                            <FavoriteIcon />
                        </IconButton>
                        <IconButton>
                            <Badge badgeContent={1} color="error">
                                <NotificationIcon />
                            </Badge>
                        </IconButton>
                        <IconButton onClick={handleLogoutClick}>
                            <MenuIcon />
                        </IconButton>
                    </div>
                </div>

                <div className="charts-grid">
                    <div className="chart-card">
                        <Typography variant="h6" gutterBottom>SUBSCRIPTIONS</Typography>
                        {/* Add your subscriptions chart here */}
                    </div>
                </div>

                <div className="calendar-section">
                    <Typography variant="h6" gutterBottom>EARNING</Typography>
                    {/* Add your earning chart here */}
                </div>
            </div>

            {/* Logout Confirmation Dialog */}
            <Dialog
                open={openLogoutDialog}
                onClose={handleLogoutCancel}
                aria-labelledby="logout-dialog-title"
            >
                <DialogTitle id="logout-dialog-title">
                    Confirm Logout
                </DialogTitle>
                <DialogContent>
                    Are you sure you want to logout?
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleLogoutCancel} color="primary">
                        Cancel
                    </Button>
                    <Button onClick={handleLogoutConfirm} color="primary" variant="contained">
                        Logout
                    </Button>
                </DialogActions>
            </Dialog>
        </div>
    );
};

export default Dashboard; 