import { useVirtualizer } from '@tanstack/react-virtual';
import { useRef } from 'react';
import ItemRow from './ItemRow';

const ItemList = ({ items, onSelect }) => {
    const parentRef = useRef<HTMLDivElement>(null);

    const rowVirtualizer = useVirtualizer({
        count: items.length,
        getScrollElement: () => parentRef.current,
        estimateSize: () => 50,
        overscan: 5,
    });

    return (
        <div
            ref={parentRef}
            style={{
                height: '500px',
                overflow: 'auto',
                width: '100%',
            }}
        >
            <div
                style={{
                    height: `${rowVirtualizer.getTotalSize()}px`,
                    position: 'relative',
                    width: '100%',
                }}
            >
                {rowVirtualizer.getVirtualItems().map((virtualRow) => (
                    <div
                        key={virtualRow.index}
                        style={{
                            position: 'absolute',
                            top: 0,
                            left: 0,
                            width: '100%',
                            height: `${virtualRow.size}px`,
                            transform: `translateY(${virtualRow.start}px)`,
                        }}
                    >
                        <ItemRow
                            item={items[virtualRow.index]}
                            onSelect={onSelect}
                        />
                    </div>
                ))}
            </div>
        </div>
    );
};

export default ItemList;