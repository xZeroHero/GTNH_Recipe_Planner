import React from 'react';
import type {IItemFluid} from '../../interface/IItemFluid';

interface ItemFluidProps {
    item: IItemFluid;
    onSelect?: (id: number) => void;
}

const ItemFluidCard: React.FC<ItemFluidProps> = ({item, onSelect}) => {
    return (
        <div
            className="item-card"
            onClick={() => onSelect?.(item.id)}
        >
            <img src={item.iconName} alt={item.name}/>
            <h3>{item.name}</h3>
            {item.oreDicts.length > 0 && (
                <div className="ore-dicts">
                    {item.oreDicts.join(', ')}
                </div>
            )}
        </div>
    );
};

export default ItemFluidCard;