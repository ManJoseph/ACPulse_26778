package com.acpulse.config;

import com.acpulse.model.Location;
import com.acpulse.model.Role;
import com.acpulse.repository.LocationRepository;
import com.acpulse.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.List;

@Component
public class LocationDataInitializer implements CommandLineRunner {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private RoleRepository roleRepository;

    // Data structure from user prompt
    private static final Map<String, List<String>> KIGALI_CITY = Map.of(
        "Gasabo", List.of("Bumbogo", "Gatsata", "Gikomero", "Gisozi", "Jabana", "Jali", "Kacyiru", "Kimihurura", "Kimironko", "Kinyinya", "Ndera", "Nduba", "Remera", "Rusororo", "Rutunga"),
        "Kicukiro", List.of("Gahanga", "Gatenga", "Gikondo", "Kagarama", "Kanombe", "Kicukiro", "Kigarama", "Masaka", "Niboye", "Nyarugunga"),
        "Nyarugenge", List.of("Gitega", "Kanyinya", "Kigali", "Kimisagara", "Mageragere", "Muhima", "Nyakabanda", "Nyamirambo", "Nyarugenge", "Rwezamenyo")
    );

    private static final Map<String, List<String>> EASTERN_PROVINCE = Map.of(
        "Bugesera", List.of("Gashora", "Juru", "Kamabuye", "Ntarama", "Mareba", "Mayange", "Musenyi", "Mwogo", "Ngeruka", "Nyamata", "Nyarugenge", "Rilima", "Ruhuha", "Rweru", "Shyara"),
        "Gatsibo", List.of("Gasange", "Gatsibo", "Gitoki", "Kabarore", "Kageyo", "Kiramuruzi", "Kiziguro", "Muhura", "Murambi", "Ngarama", "Nyagihanga", "Remera", "Rugarama", "Rwimbogo"),
        "Kayonza", List.of("Gahini", "Kabare", "Kabarondo", "Mukarange", "Murama", "Murundi", "Mwiri", "Ndego", "Nyamirama", "Rukara", "Ruramira", "Rwinkwavu"),
        "Kirehe", List.of("Mahama", "Mpanga", "Musaza", "Mushikiri", "Nasho", "Nyamugari", "Nyarubuye", "Kigarama", "Gahara", "Gatore", "Kirehe", "Kigina"),
        "Ngoma", List.of("Gashanda", "Jarama", "Karembo", "Kazo", "Kibungo", "Mugesera", "Murama", "Mutenderi", "Remera", "Rukira", "Rukumberi", "Rurenge", "Sake", "Zaza"),
        "Nyagatare", List.of("Gatunda", "Kiyombe", "Karama", "Karangazi", "Katabagemu", "Matimba", "Mimuli", "Mukama", "Musheli", "Nyagatare", "Rukomo", "Rwempasha", "Rwimiyaga", "Tabagwe"),
        "Rwamagana", List.of("Fumbwe", "Gahengeri", "Gishali", "Karenge", "Kigabiro", "Muhazi", "Munyaga", "Munyiginya", "Musha", "Muyumbu", "Mwulire", "Nyakariro", "Nzige", "Rubona")
    );

    private static final Map<String, List<String>> NORTHERN_PROVINCE = Map.of(
        "Burera", List.of("Bungwe", "Butaro", "Cyanika", "Cyeru", "Gahunga", "Gatebe", "Gitovu", "Kagogo", "Kinoni", "Kinyababa", "Kivuye", "Nemba", "Rugarama", "Rugendabari", "Ruhunde", "Rusarabuge", "Rwerere"),
        "Gakenke", List.of("Busengo", "Coko", "Cyabingo", "Gakenke", "Gashenyi", "Mugunga", "Janja", "Kamubuga", "Karambo", "Kivuruga", "Mataba", "Minazi", "Muhondo", "Muyongwe", "Muzo", "Nemba", "Ruli", "Rusasa", "Rushashi"),
        "Gicumbi", List.of("Bukure", "Bwisige", "Byumba", "Cyumba", "Giti", "Kaniga", "Manyagiro", "Miyove", "Kageyo", "Mukarange", "Muko", "Mutete", "Nyamiyaga", "Nyankenke", "Rubaya", "Rukomo", "Rushaki", "Rutare", "Ruvune", "Shangasha"),
        "Musanze", List.of("Busogo", "Cyuve", "Gacaca", "Gashaki", "Gataraga", "Kimonyi", "Kinigi", "Muhoza", "Muko", "Musanze", "Nkotsi", "Nyange", "Remera", "Rwaza", "Shingiro"),
        "Rulindo", List.of("Base", "Burega", "Bushoki", "Buyoga", "Cyinzuzi", "Cyungo", "Kinihira", "Kisaro", "Masoro", "Mbogo", "Murambi", "Ngoma", "Ntarabana", "Rukozo", "Rusiga", "Shyorongi", "Tumba")
    );

    private static final Map<String, List<String>> SOUTHERN_PROVINCE = Map.of(
        "Gisagara", List.of("Gikonko", "Gishubi", "Kansi", "Kibilizi", "Kigembe", "Mamba", "Muganza", "Mugombwa", "Mukindo", "Musha", "Ndora", "Nyanza", "Save"),
        "Huye", List.of("Gishamvu", "Karama", "Kigoma", "Kinazi", "Maraba", "Mbazi", "Mukura", "Ngoma", "Ruhashya", "Rusatira", "Simbi", "Tumba", "Huye"),
        "Kamonyi", List.of("Gacurabwenge", "Karama", "Kayenzi", "Kayumbu", "Mugina", "Musambira", "Ngamba", "Nyamiyaga", "Nyarubaka", "Rugarika", "Rukoma", "Runda"),
        "Muhanga", List.of("Cyeza", "Kabacuzi", "Kibangu", "Kiyumba", "Muhanga", "Mushishiro", "Nyabinoni", "Nyamabuye", "Nyarusange", "Rongi", "Rugendabari", "Shyogwe"),
        "Nyamagabe", List.of("Buruhukiro", "Cyanika", "Gatare", "Kaduha", "Kamegeli", "Kibirizi", "Kibumbwe", "Kitabi", "Mbazi", "Mugano", "Musange", "Musebeya", "Mushubi", "Nkomane", "Gasaka", "Tare", "Uwinkingi"),
        "Nyanza", List.of("Busasamana", "Busoro", "Cyabakamyi", "Kibirizi", "Kigoma", "Mukingo", "Muyira", "Ntyazo", "Nyagisozi"),
        "Nyaruguru", List.of("Cyahinda", "Busanze", "Kibeho", "Mata", "Munini", "Kivu", "Ngera", "Ngoma", "Nyabimata", "Nyagisozi", "Ruheru", "Muganza", "Ruramba", "Rusenge"),
        "Ruhango", List.of("Bweramana", "Byimana", "Kabagari", "Kinazi", "Kinihira", "Mbuye", "Mwendo", "Ntongwe", "Ruhango")
    );

    private static final Map<String, List<String>> WESTERN_PROVINCE = Map.of(
        "Karongi", List.of("Bwishyura", "Gishari", "Gishyita", "Gisovu", "Gitesi", "Mubuga", "Murambi", "Murundi", "Mutuntu", "Rugabano", "Ruganda", "Rofnkuba", "Twumba"),
        "Ngororero", List.of("Bwira", "Gatumba", "Hindiro", "Kabaya", "Kageyo", "Kavumu", "Matyazo", "Muhanda", "Muhororo", "Ndaro", "Ngororero", "Nyange", "Sovu"),
        "Nyabihu", List.of("Bigogwe", "Jenda", "Jomba", "Kabatof", "Karago", "Kintobo", "Mukamira", "Muringa", "Rambura", "Rugera", "Rurembo", "Shyira"),
        "Nyamasheke", List.of("Bushekeri", "Bushenge", "Cyato", "Gihombo", "Kagano", "Kanjongo", "Karambi", "Karengera", "Kirimbi", "Macuba", "Nyabitekeri", "Mahembe", "Rangiro", "Shangi", "Ruharambuga"),
        "Rubavu", List.of("Bugeshi", "Busasamana", "Cyanzarwe", "Gisenyi", "Kanama", "Kanzenze", "Mudende", "Nyakiliba", "Nyamyumba", "Nyundo", "Rubavu", "Rugerero"),
        "Rusizi", List.of("Bugarama", "Butare", "Bweyeye", "Gikundamvura", "Gashonga", "Giheke", "Gihundwe", "Gitambi", "Kamembe", "Muganza", "Mururu", "Nkanka", "Nkombo", "Nkungu", "Nyakabuye", "Nyakarenzo", "Nzahaha", "Rwimbogo"),
        "Rutsiro", List.of("Boneza", "Gihango", "Kigeyo", "Kivumu", "Manihira", "Mukura", "Murunda", "Musasa", "Mushonyi", "Mushubati", "Nyabirasi", "Ruhango", "Rusebeya")
    );

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        initializeRoles();
        initializeLocations();
    }

    private void initializeRoles() {
        System.out.println("Checking for existing roles...");
        if (roleRepository.count() == 0) {
            System.out.println("!!! ROLES TABLE IS EMPTY. CREATING DEFAULT ROLES... !!!");
            
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
            System.out.println("  ✓ Roles already exist. No action needed.");
        }
    }

    private void initializeLocations() {
        if (locationRepository.count() == 0) {
            System.out.println("🌱 Initializing Rwandan location data...");
            createProvince("City of Kigali", "KGL", KIGALI_CITY);
            createProvince("Eastern Province", "EST", EASTERN_PROVINCE);
            createProvince("Northern Province", "NTH", NORTHERN_PROVINCE);
            createProvince("Southern Province", "STH", SOUTHERN_PROVINCE);
            createProvince("Western Province", "WST", WESTERN_PROVINCE);
            System.out.println("✅ Location data initialized successfully.");
        }
    }

    private void createProvince(String provinceName, String provinceCode, Map<String, List<String>> districts) {
        Location province = new Location();
        province.setName(provinceName);
        province.setCode(provinceCode);
        province.setType(Location.LocationType.PROVINCE);
        locationRepository.save(province);

        districts.forEach((districtName, sectors) -> createDistrict(districtName, province, sectors));
    }

    private void createDistrict(String districtName, Location parentProvince, List<String> sectors) {
        Location district = new Location();
        district.setName(districtName);
        district.setCode(parentProvince.getCode() + "_" + districtName.toUpperCase().replaceAll("\\s+", "_"));
        district.setType(Location.LocationType.DISTRICT);
        district.setParent(parentProvince);
        locationRepository.save(district);

        sectors.forEach(sectorName -> createSector(sectorName, district));
    }

    private void createSector(String sectorName, Location parentDistrict) {
        Location sector = new Location();
        sector.setName(sectorName);
        sector.setCode(parentDistrict.getCode() + "_" + sectorName.toUpperCase().replaceAll("\\s+", "_"));
        sector.setType(Location.LocationType.SECTOR);
        sector.setParent(parentDistrict);
        locationRepository.save(sector);
    }
}
