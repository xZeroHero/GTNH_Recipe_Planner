import { memo } from 'react';
import { Box, Typography } from '@mui/material';
import type { IItemFluid } from '../../../interface/IItemFluid';

interface ItemRowProps {
    item: IItemFluid;
    onSelect: (item: IItemFluid) => void;
}

const ItemRow = memo(({ item, onSelect }: ItemRowProps) => (
    <Box
        onClick={() => onSelect(item)}
        sx={{
            display: 'flex',
            alignItems: 'center',
            p: 1,
            '&:hover': { bgcolor: 'action.hover' },
            cursor: 'pointer'
        }}
    >
        <img
            src={item.iconName}
            alt={item.name}
            style={{ width: 32, height: 32, marginRight: 8 }}
        />
        <Typography noWrap>{item.name}</Typography>
    </Box>
));

export default ItemRow;