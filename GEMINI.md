# Blank Page Error Fix (RoomCard.jsx)

## Problem:

`TypeError: Cannot read properties of undefined (reading 'name')` or similar errors leading to a blank page when rendering `RoomCard` components. This occurs when the `room` prop passed to `RoomCard` (or its nested properties like `room.location` or `room.occupiedBy`) is `undefined` or `null`, or when the `RoomCard` component attempts to access properties with names that do not match the actual data structure received from the backend.

## Solution:

The fix involves two main parts:

1.  **Defensive Rendering:** Add a check at the beginning of the `RoomCard` component to prevent rendering if the `room` prop itself is `null` or `undefined`.
2.  **Correct Property Mapping with Optional Chaining:** Update property access within the `RoomCard` component to match the actual backend data structure, using optional chaining (`?.`) for nested properties to prevent errors if intermediate objects are `null` or `undefined`.

### Code Changes in `acpulse-frontend/acpulse-frontend/src/components/rooms/RoomCard.jsx`:

**Old Code (Problematic Snippet Example):**

```jsx
const RoomCard = ({ room }) => {
  const navigate = useNavigate();
  // ...
  return (
    // ...
      <h3>{room.name}</h3>
    // ...
      <span>{room.location.name}</span>
    // ...
      <span>Occupied by <strong>{room.occupiedBy.name}</strong></span>
    // ...
  );
};
```

**New Code (Fixed Version):**

```jsx
const RoomCard = ({ room }) => {
  // 1. Defensive Rendering:
  if (!room) return null;

  const navigate = useNavigate();
  // ...

  return (
    // ...
      // 2. Correct Property Mapping with Optional Chaining:
      <h3 className="text-lg font-bold">{room?.roomName}</h3> {/* Changed from room.name */}
    // ...
      <span>{room?.building} - {room?.floor}</span> {/* Changed from room.location.name */}
    // ...
      {room.status === ROOM_STATUS.OCCUPIED && room.currentLecturerName ? (
        <div className="flex items-center gap-2 text-sm">
          <User className="w-4 h-4 text-red-500" />
          <span>Occupied by <strong>{room.currentLecturerName}</strong></span> {/* Changed from room.occupiedBy.name */}
        </div>
      ) : (
        // ... (rest of the conditional rendering)
      )}
    // ...
  );
};
```

**Explanation:**

*   `if (!room) return null;`: Ensures that the component doesn't attempt to render anything if `room` is not provided, preventing `TypeError` on `room.something`.
*   `room?.roomName` (and `room?.building`, `room?.floor`, `room.currentLecturerName`): Updates the property names to `roomName`, `building`, `floor`, and `currentLecturerName` which were identified as the correct property names from the backend data. Optional chaining (`?.`) is added for robustness, although the `if (!room)` check already covers the top-level `room` object.
*   The conditional rendering for "Occupied by" now checks `room.currentLecturerName` instead of `room.occupiedBy` since `occupiedBy` was not an object in the backend response.

This combination ensures that the `RoomCard` component can handle cases where data might be missing or properties are named differently than initially assumed, preventing crashes and blank pages.
