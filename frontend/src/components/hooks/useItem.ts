import { useState, useEffect } from 'react';
import type { IItemFluid } from '../../interface/IItemFluid';

const API_BASE_URL = 'http://localhost:8080/api';

export const useItems = () => {
    const [items, setItems] = useState<IItemFluid[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<Error | null>(null);

    useEffect(() => {
        const fetchItems = async () => {
            try {
                setLoading(true);
                const response = await fetch(`${API_BASE_URL}/items-fluids`, {
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json',
                    },
                });

                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }

                const data: IItemFluid[] = await response.json();
                setItems(data);
                setError(null);
            } catch (err) {
                console.error('Failed to fetch items:', err);
                setError(err instanceof Error ? err : new Error('Failed to fetch items'));
                setItems([]);
            } finally {
                setLoading(false);
            }
        };

        fetchItems();
    }, []);

    return { items, loading, error };
};