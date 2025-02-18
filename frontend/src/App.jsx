import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Register from './components/Register';
import Login from './components/Login';
import Dashboard from './components/Dashboard';
import Settings from './components/Settings';
import { createTheme, ThemeProvider } from '@mui/material';

const theme = createTheme();

function App() {
    return (
        <ThemeProvider theme={theme}>
            <Router>
                <Routes>
                    <Route path="/register" element={<Register />} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/dashboard" element={<Dashboard />} />
                    <Route path="/settings" element={<Settings />} />
                    <Route path="/" element={<Navigate to="/login" />} />
                </Routes>
            </Router>
        </ThemeProvider>
    );
}

export default App; 