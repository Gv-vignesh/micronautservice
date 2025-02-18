import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../services/authService';
import {
    Paper,
    TextField,
    Button,
    Typography,
    Box,
    Alert
} from '@mui/material';
import '../styles/auth.css';

const Register = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await authService.register(username, password);
            navigate('/login', { state: { message: 'Registration successful! Please login.' } });
        } catch (err) {
            setError(err.message);
        }
    };

    return (
        <div className="auth-container">
            <Paper elevation={3} className="auth-card">
                <div className="auth-content">
                    <Typography variant="h4" className="auth-title">
                        Create Account
                    </Typography>
                    {error && (
                        <Alert severity="error" sx={{ mb: 2 }}>
                            {error}
                        </Alert>
                    )}
                    <Box component="form" onSubmit={handleSubmit}>
                        <TextField
                            fullWidth
                            label="Username"
                            variant="filled"
                            className="auth-input"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                        />
                        <TextField
                            fullWidth
                            label="Password"
                            type="password"
                            variant="filled"
                            className="auth-input"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                        <Button
                            type="submit"
                            variant="contained"
                            fullWidth
                            className="auth-button"
                        >
                            REGISTER
                        </Button>
                        <span 
                            className="auth-link"
                            onClick={() => navigate('/login')}
                        >
                            Already have an account? Login →
                        </span>
                    </Box>
                </div>
            </Paper>
        </div>
    );
};

export default Register; 