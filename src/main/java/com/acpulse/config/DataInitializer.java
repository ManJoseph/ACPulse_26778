package com.acpulse.config;

import com.acpulse.model.*;
import com.acpulse.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * DataInitializer
 *
 * Initializes the database with sample data when the application starts.
 *
 * @Component - Tells Spring this is a component to be managed.
 * CommandLineRunner - Interface that allows running code after application startup.
 *
 * This class runs automatically after Spring Boot starts and initializes:
 * - Default roles (ADMIN, STUDENT, LECTURER, STAFF)
 * - Sample admin user
 * - Sample Rwanda locations
 * - Sample rooms
 * - Sample offices
 * - Current semester
 */
@Component
public class DataInitializer implements CommandLineRunner {

    /**
     * @Autowired - Tells Spring to automatically inject the repositories.
     */
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private OfficeRepository officeRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    /**
     * This method runs automatically after the application starts.
     *
     * @param args Command line arguments
     */
    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("🔧 Initializing Database with Sample Data...");
        System.out.println("=".repeat(80));

        initializeRoles();
        initializeRwandaLocations();
        initializeAdminUser();
        initializeRooms();
        initializeOffices();
        initializeSemesters();

        System.out.println("✅ Database initialization completed!");
        System.out.println("=".repeat(80) + "\n");
    }

    /**
     * Initialize default roles (ADMIN, STUDENT, LECTURER, STAFF).
     */
    private void initializeRoles() {
        if (roleRepository.count() == 0) {
            System.out.println("📋 Creating default roles...");

            Role studentRole = new Role("STUDENT");
            studentRole.setDescription("Student user with search and view permissions");
            roleRepository.save(studentRole);

            Role lecturerRole = new Role("LECTURER");
            lecturerRole.setDescription("Lecturer with room occupation and status update permissions");
            roleRepository.save(lecturerRole);

            Role staffRole = new Role("STAFF");
            staffRole.setDescription("Staff member with office management permissions");
            roleRepository.save(staffRole);

            Role adminRole = new Role("ADMIN");
            adminRole.setDescription("Administrator with full system access");
            roleRepository.save(adminRole);

            System.out.println("  ✓ Created roles: STUDENT, LECTURER, STAFF, ADMIN");
        } else {
            System.out.println("  ℹ️  Roles already exist");
        }
    }

    /**
     * Initialize sample Rwanda locations.
     */
    private void initializeRwandaLocations() {
        if (locationRepository.count() == 0) {
            System.out.println("🗺️ Creating Rwanda locations...");

            // Create provinces
            Location kigali = new Location();
            kigali.setName("Kigali");
            kigali.setCode("KGL");
            kigali.setType(Location.LocationType.PROVINCE);
            kigali.setCreatedAt(LocalDateTime.now());
            locationRepository.save(kigali);

            Location eastern = new Location();
            eastern.setName("Eastern Province");
            eastern.setCode("EST");
            eastern.setType(Location.LocationType.PROVINCE);
            eastern.setCreatedAt(LocalDateTime.now());
            locationRepository.save(eastern);

            Location northern = new Location();
            northern.setName("Northern Province");
            northern.setCode("NTH");
            northern.setType(Location.LocationType.PROVINCE);
            northern.setCreatedAt(LocalDateTime.now());
            locationRepository.save(northern);

            Location southern = new Location();
            southern.setName("Southern Province");
            southern.setCode("STH");
            southern.setType(Location.LocationType.PROVINCE);
            southern.setCreatedAt(LocalDateTime.now());
            locationRepository.save(southern);

            Location western = new Location();
            western.setName("Western Province");
            western.setCode("WST");
            western.setType(Location.LocationType.PROVINCE);
            western.setCreatedAt(LocalDateTime.now());
            locationRepository.save(western);

            // Create Kigali districts (parent: Kigali)
            Location gasabo = new Location();
            gasabo.setName("Gasabo");
            gasabo.setCode("GSB");
            gasabo.setType(Location.LocationType.DISTRICT);
            gasabo.setParentId(kigali.getId());
            gasabo.setCreatedAt(LocalDateTime.now());
            locationRepository.save(gasabo);

            Location kicukiro = new Location();
            kicukiro.setName("Kicukiro");
            kicukiro.setCode("KCK");
            kicukiro.setType(Location.LocationType.DISTRICT);
            kicukiro.setParentId(kigali.getId());
            kicukiro.setCreatedAt(LocalDateTime.now());
            locationRepository.save(kicukiro);

            Location nyarugenge = new Location();
            nyarugenge.setName("Nyarugenge");
            nyarugenge.setCode("NYR");
            nyarugenge.setType(Location.LocationType.DISTRICT);
            nyarugenge.setParentId(kigali.getId());
            nyarugenge.setCreatedAt(LocalDateTime.now());
            locationRepository.save(nyarugenge);

            System.out.println("  ✓ Created sample Rwanda locations hierarchy");
        } else {
            System.out.println("  ℹ️  Locations already exist");
        }
    }

    /**
     * Initialize sample admin user.
     */
    private void initializeAdminUser() {
        if (userRepository.findByEmail("admin@auca.ac.rw").isEmpty()) {
            System.out.println("👤 Creating admin user...");

            Role adminRole = roleRepository.findByRoleName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

            // Get the default location (e.g., Kigali)
            Location kigali = locationRepository.findByCode("KGL")
                    .orElseThrow(() -> new RuntimeException("Default location not found"));

            User admin = new User();
            admin.setName("System Admin");
            admin.setEmail("admin@auca.ac.rw");
            admin.setPasswordHash("Admin123!");
            admin.setIdentificationNumber("ADMIN001");
            admin.setStatus(User.UserStatus.APPROVED);
            admin.setRole(adminRole);
            admin.setLocation(kigali);
            admin.setCreatedAt(LocalDateTime.now());

            userRepository.save(admin);

            System.out.println("  ✓ Admin user created with location: " + kigali.getName());
        } else {
            System.out.println("  ℹ️  Admin user already exists");
        }
    }

    /**
     * Initialize sample rooms.
     */
    private void initializeRooms() {
        if (roomRepository.count() == 0) {
            System.out.println("🏢 Creating sample rooms...");

            Room r1 = new Room();
            r1.setRoomNumber("A-101");
            r1.setRoomName("Lecture Hall 1");
            r1.setCapacity(100);
            r1.setBuilding("Academic Block A");
            r1.setFloor("1st Floor");
            r1.setRoomType(Room.RoomType.LECTURE_HALL);
            r1.setStatus(Room.RoomStatus.AVAILABLE);
            r1.setCreatedAt(LocalDateTime.now());
            roomRepository.save(r1);

            Room r2 = new Room();
            r2.setRoomNumber("A-102");
            r2.setRoomName("Lecture Hall 2");
            r2.setCapacity(100);
            r2.setBuilding("Academic Block A");
            r2.setFloor("1st Floor");
            r2.setRoomType(Room.RoomType.LECTURE_HALL);
            r2.setStatus(Room.RoomStatus.AVAILABLE);
            r2.setCreatedAt(LocalDateTime.now());
            roomRepository.save(r2);

            Room r3 = new Room();
            r3.setRoomNumber("A-204");
            r3.setRoomName("Computer Lab 1");
            r3.setCapacity(40);
            r3.setBuilding("Academic Block A");
            r3.setFloor("2nd Floor");
            r3.setRoomType(Room.RoomType.LAB);
            r3.setStatus(Room.RoomStatus.AVAILABLE);
            r3.setCreatedAt(LocalDateTime.now());
            roomRepository.save(r3);

            Room r4 = new Room();
            r4.setRoomNumber("A-205");
            r4.setRoomName("Computer Lab 2");
            r4.setCapacity(40);
            r4.setBuilding("Academic Block A");
            r4.setFloor("2nd Floor");
            r4.setRoomType(Room.RoomType.LAB);
            r4.setStatus(Room.RoomStatus.AVAILABLE);
            r4.setCreatedAt(LocalDateTime.now());
            roomRepository.save(r4);

            Room r5 = new Room();
            r5.setRoomNumber("B-101");
            r5.setRoomName("Science Lab");
            r5.setCapacity(30);
            r5.setBuilding("Academic Block B");
            r5.setFloor("1st Floor");
            r5.setRoomType(Room.RoomType.LAB);
            r5.setStatus(Room.RoomStatus.AVAILABLE);
            r5.setCreatedAt(LocalDateTime.now());
            roomRepository.save(r5);

            Room r6 = new Room();
            r6.setRoomNumber("B-201");
            r6.setRoomName("Conference Room");
            r6.setCapacity(50);
            r6.setBuilding("Academic Block B");
            r6.setFloor("2nd Floor");
            r6.setRoomType(Room.RoomType.MEETING_ROOM);
            r6.setStatus(Room.RoomStatus.AVAILABLE);
            r6.setCreatedAt(LocalDateTime.now());
            roomRepository.save(r6);

            System.out.println("  ✓ Created sample rooms: A-101, A-102, A-204, A-205, B-101, B-201");
        } else {
            System.out.println("  ℹ️  Rooms already exist");
        }
    }

    /**
     * Initialize sample offices.
     */
    private void initializeOffices() {
        if (officeRepository.count() == 0) {
            System.out.println("🏢 Creating sample offices...");

            Office o1 = new Office();
            o1.setOfficeNumber("Admin-101");
            o1.setOfficeName("Registrar Office");
            o1.setBuilding("Admin Building");
            o1.setFloor("1st Floor");
            o1.setAvailabilityStatus(Office.AvailabilityStatus.CLOSED);
            o1.setRegularOpenTime(LocalTime.of(8, 0));
            o1.setRegularCloseTime(LocalTime.of(17, 0));
            o1.setCreatedAt(LocalDateTime.now());
            officeRepository.save(o1);

            Office o2 = new Office();
            o2.setOfficeNumber("Admin-102");
            o2.setOfficeName("Finance Office");
            o2.setBuilding("Admin Building");
            o2.setFloor("1st Floor");
            o2.setAvailabilityStatus(Office.AvailabilityStatus.CLOSED);
            o2.setRegularOpenTime(LocalTime.of(8, 0));
            o2.setRegularCloseTime(LocalTime.of(17, 0));
            o2.setCreatedAt(LocalDateTime.now());
            officeRepository.save(o2);

            Office o3 = new Office();
            o3.setOfficeNumber("Admin-201");
            o3.setOfficeName("Student Affairs");
            o3.setBuilding("Admin Building");
            o3.setFloor("2nd Floor");
            o3.setAvailabilityStatus(Office.AvailabilityStatus.CLOSED);
            o3.setRegularOpenTime(LocalTime.of(8, 0));
            o3.setRegularCloseTime(LocalTime.of(17, 0));
            o3.setCreatedAt(LocalDateTime.now());
            officeRepository.save(o3);

            Office o4 = new Office();
            o4.setOfficeNumber("Admin-202");
            o4.setOfficeName("Academic Affairs");
            o4.setBuilding("Admin Building");
            o4.setFloor("2nd Floor");
            o4.setAvailabilityStatus(Office.AvailabilityStatus.CLOSED);
            o4.setRegularOpenTime(LocalTime.of(8, 0));
            o4.setRegularCloseTime(LocalTime.of(17, 0));
            o4.setCreatedAt(LocalDateTime.now());
            officeRepository.save(o4);

            System.out.println("  ✓ Created sample offices: Admin-101, Admin-102, Admin-201, Admin-202");
        } else {
            System.out.println("  ℹ️  Offices already exist");
        }
    }

    /**
     * Initialize current semester.
     */
    private void initializeSemesters() {
        if (semesterRepository.count() == 0) {
            System.out.println("📅 Creating current semester...");

            Semester semester = new Semester();
            semester.setName("Fall 2024/2025");
            semester.setStartDate(LocalDate.of(2024, 9, 1));
            semester.setEndDate(LocalDate.of(2025, 1, 31));
            semester.setIsCurrent(true);
            semester.setCreatedAt(LocalDateTime.now());
            semesterRepository.save(semester);

            System.out.println("  ✓ Created semester: Fall 2024/2025");
        } else {
            System.out.println("  ℹ️  Semesters already exist");
        }
    }
}