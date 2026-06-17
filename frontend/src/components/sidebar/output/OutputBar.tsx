// components/sidebar/Sidebar.tsx
import { useState } from 'react';
import { Box, Button } from '@mui/material'; // or your UI library
import ItemSearch from './ItemSearch';

const OutputBar = () => {
    const [isSearchOpen, setIsSearchOpen] = useState(false);

    return (
        <Box sx={{
            position: 'absolute',
            right: 0,
            top: '50%',
            transform: 'translateY(-50%)',
            width: 300,
            height: '80vh',
            bgcolor: 'background.paper',
            p: 2,
            boxShadow: 3,
            borderRadius: 2,
            overflow: 'hidden'
        }}>
            {isSearchOpen ? (
                <ItemSearch onClose={() => setIsSearchOpen(false)} />
            ) : (
                <Button
                    variant="contained"
                    fullWidth
                    onClick={() => setIsSearchOpen(true)}
                >
                    Add Item
                </Button>
            )}
        </Box>
    );
};

export default OutputBar;