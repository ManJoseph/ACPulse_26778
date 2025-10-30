-- Insert default roles
INSERT INTO roles (role_name, description) VALUES
('STUDENT', 'Student user with search and view permissions'),
('LECTURER', 'Lecturer with room occupation and status update permissions'),
('STAFF', 'Staff member with office management permissions'),
('ADMIN', 'Administrator with full system access')
ON CONFLICT (role_name) DO NOTHING;

-- Insert default admin user
-- Password: Admin123! (plain text to match your AuthService)
INSERT INTO users (name, email, password_hash, identification_number, status, role_id, created_at)
SELECT
    'System Admin',
    'admin@auca.ac.rw',
    'Admin123!',  -- Plain text for your current plain-text comparison
    'ADMIN001',
    'APPROVED',
    r.id,
    CURRENT_TIMESTAMP
FROM roles r
WHERE r.role_name = 'ADMIN'
ON CONFLICT (email) DO NOTHING;

-- Insert Rwanda provinces
INSERT INTO locations (name, code, type, parent_id, created_at) VALUES
('Kigali', 'KGL', 'PROVINCE', NULL, CURRENT_TIMESTAMP),
('Eastern Province', 'EST', 'PROVINCE', NULL, CURRENT_TIMESTAMP),
('Northern Province', 'NTH', 'PROVINCE', NULL, CURRENT_TIMESTAMP),
('Southern Province', 'STH', 'PROVINCE', NULL, CURRENT_TIMESTAMP),
('Western Province', 'WST', 'PROVINCE', NULL, CURRENT_TIMESTAMP)
ON CONFLICT (code) DO NOTHING;

-- Insert Kigali districts
INSERT INTO locations (name, code, type, parent_id, created_at)
SELECT 'Gasabo', 'GSB', 'DISTRICT', l.id, CURRENT_TIMESTAMP
FROM locations l WHERE l.code = 'KGL'
ON CONFLICT (code) DO NOTHING;

INSERT INTO locations (name, code, type, parent_id, created_at)
SELECT 'Kicukiro', 'KCK', 'DISTRICT', l.id, CURRENT_TIMESTAMP
FROM locations l WHERE l.code = 'KGL'
ON CONFLICT (code) DO NOTHING;

INSERT INTO locations (name, code, type, parent_id, created_at)
SELECT 'Nyarugenge', 'NYR', 'DISTRICT', l.id, CURRENT_TIMESTAMP
FROM locations l WHERE l.code = 'KGL'
ON CONFLICT (code) DO NOTHING;

-- Insert sample rooms
INSERT INTO rooms (room_number, room_name, capacity, building, floor, room_type, status, created_at) VALUES
('A-101', 'Lecture Hall 1', 100, 'Academic Block A', '1st Floor', 'LECTURE_HALL', 'AVAILABLE', CURRENT_TIMESTAMP),
('A-102', 'Lecture Hall 2', 100, 'Academic Block A', '1st Floor', 'LECTURE_HALL', 'AVAILABLE', CURRENT_TIMESTAMP),
('A-204', 'Computer Lab 1', 40, 'Academic Block A', '2nd Floor', 'LAB', 'AVAILABLE', CURRENT_TIMESTAMP),
('A-205', 'Computer Lab 2', 40, 'Academic Block A', '2nd Floor', 'LAB', 'AVAILABLE', CURRENT_TIMESTAMP),
('B-101', 'Science Lab', 30, 'Academic Block B', '1st Floor', 'LAB', 'AVAILABLE', CURRENT_TIMESTAMP),
('B-201', 'Conference Room', 50, 'Academic Block B', '2nd Floor', 'MEETING_ROOM', 'AVAILABLE', CURRENT_TIMESTAMP)
ON CONFLICT (room_number) DO NOTHING;

-- Insert sample offices
INSERT INTO offices (office_number, office_name, building, floor, availability_status, regular_open_time, regular_close_time, created_at) VALUES
('Admin-101', 'Registrar Office', 'Admin Building', '1st Floor', 'CLOSED', '08:00:00', '17:00:00', CURRENT_TIMESTAMP),
('Admin-102', 'Finance Office', 'Admin Building', '1st Floor', 'CLOSED', '08:00:00', '17:00:00', CURRENT_TIMESTAMP),
('Admin-201', 'Student Affairs', 'Admin Building', '2nd Floor', 'CLOSED', '08:00:00', '17:00:00', CURRENT_TIMESTAMP),
('Admin-202', 'Academic Affairs', 'Admin Building', '2nd Floor', 'CLOSED', '08:00:00', '17:00:00', CURRENT_TIMESTAMP)
ON CONFLICT (office_number) DO NOTHING;

-- Insert current semester
INSERT INTO semesters (name, start_date, end_date, is_current, created_at) VALUES
('Fall 2024/2025', '2024-09-01', '2025-01-31', true, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;