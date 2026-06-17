// components/sidebar/ItemSearch.tsx
import { useState, useMemo } from 'react';
import { Box, TextField, IconButton } from '@mui/material';
import { Close } from '@mui/icons-material';
import ItemList from './ItemList';
import { useItems } from '../../hooks/useItem.ts';
import { CircularProgress } from '@mui/material';

const ItemSearch = ({ onClose }) => {
    const [searchTerm, setSearchTerm] = useState('');
    const { items, loading, error } = useItems();

    const filteredItems = useMemo(() => {
        if (!searchTerm) return items;
        const term = searchTerm.toLowerCase();
        return items.filter((item) =>
            item.name.toLowerCase().includes(term) ||
            item.unlocalizedName?.toLowerCase().includes(term)
        );
    }, [items, searchTerm]);

    if (error) {
        return (
            <Box sx={{ p: 2, color: 'error.main' }}>
                Error loading items: {error.message}
            </Box>
        );
    }

    return (
        <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
            <Box sx={{ display: 'flex', gap: 1, mb: 2 }}>
                <TextField
                    fullWidth
                    size="small"
                    placeholder="Search items..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    autoFocus
                    disabled={loading}
                />
                <IconButton onClick={onClose} disabled={loading}>
                    <Close />
                </IconButton>
            </Box>
            {loading ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', p: 2 }}>
                    <CircularProgress size={24} />
                </Box>
            ) : (
                <ItemList
                    items={filteredItems}
                    onSelect={(item) => {
                        onClose();
                        // Handle item selection here
                    }}
                />
            )}
        </Box>
    );
};
export default ItemSearch;