import { useState, useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import roomService from '../services/roomService';
import useDebounce from './useDebounce';

const useRooms = (initialFilters = {}) => {
  const [filters, setFilters] = useState(initialFilters);
  const debouncedFilters = useDebounce(filters, 500);

  const { data, isLoading, error, isFetching } = useQuery({
    queryKey: ['rooms', debouncedFilters],
    queryFn: () => roomService.getRooms(debouncedFilters),
    keepPreviousData: true,
  });

  return {
    rooms: data || [],
    page: null, // backend returns list, not paginated
    isLoading,
    isFetching,
    error,
    filters,
    setFilters,
  };
};

export default useRooms;
