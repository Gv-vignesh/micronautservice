import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { authService } from '../services/authService';
import {
    Container,
    Paper,
    TextField,
    Button,
    Typography,
    Box,
    Alert
} from '@mui/material';
import '../styles/auth.css';

const Login = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();
    const location = useLocation();
    const message = location.state?.message;

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await authService.login(username, password);
            navigate('/dashboard');
        } catch (err) {
            setError(err.message);
        }
    };

    return (
        <div className="auth-container">
            <Paper elevation={3} className="auth-card">
                <div className="auth-content">
                    <Typography variant="h4" className="auth-title">
                        User Login
                    </Typography>
                    {message && (
                        <Alert severity="success" sx={{ mb: 2 }}>
                            {message}
                        </Alert>
                    )}
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
                        <div className="forgot-links">
                            <span className="auth-link">Forgot Username?</span>
                            <span className="auth-link">Password?</span>
                        </div>
                        <Button
                            type="submit"
                            variant="contained"
                            fullWidth
                            className="auth-button"
                        >
                            LOGIN
                        </Button>
                        <span 
                            className="auth-link"
                            onClick={() => navigate('/register')}
                        >
                            Create Your Account →
                        </span>
                    </Box>
                </div>
            </Paper>
        </div>
    );
};

export default Login; 