import React, { useState, useEffect } from 'react';
import { Avatar } from '@mui/material';

const ProfileImage = ({ imageUrl, size = 150, ...props }) => {
    const [imageSrc, setImageSrc] = useState(null);

    useEffect(() => {
        const fetchImage = async () => {
            if (!imageUrl) return;
            
            try {
                const response = await fetch(imageUrl, {
                    headers: {
                        'Authorization': `Bearer ${localStorage.getItem('token')}`
                    }
                });
                
                if (response.ok) {
                    const blob = await response.blob();
                    const objectUrl = URL.createObjectURL(blob);
                    setImageSrc(objectUrl);
                }
            } catch (error) {
                console.error('Error fetching profile image:', error);
            }
        };

        fetchImage();

        // Cleanup
        return () => {
            if (imageSrc) {
                URL.revokeObjectURL(imageSrc);
            }
        };
    }, [imageUrl]);

    return (
        <Avatar
            src={imageSrc}
            sx={{ 
                width: size, 
                height: size,
                bgcolor: '#6f42c1',
                ...props.sx
            }}
            {...props}
        />
    );
};

export default ProfileImage; 