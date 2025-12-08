import { useState, useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import roomService from '../services/roomService';
import useDebounce from './useDebounce';

const useRooms = (initialFilters = {}) => {
  const [filters, setFilters] = useState(initialFilters);
  const debouncedFilters = useDebounce(filters, 500);

  const { data, isLoading, error, isFetching } = useQuery(
    ['rooms', debouncedFilters],
    () => roomService.getRooms(debouncedFilters),
    {
      keepPreviousData: true,
    }
  );

  return {
    rooms: data?.content || [],
    page: data,
    isLoading,
    isFetching,
    error,
    filters,
    setFilters,
  };
};

export default useRooms;
