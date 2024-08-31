package gg.rohan.narwhal.util.newapi;

public class NewApiUrls {

    public static final String SINGLE_PACKET_INFO = "inventory/packet/get/{rfid}";
    public static final String MULTI_PACKET_INFO = "inventory/packet/getall";
    public static final String UPDATE_PACKET = "inventory/packet/update";

    public static final String JOBS = "pms/jobs/get";

    public static final String JOB_SPARE_PARTS = "pms/jobs/get_parts/{code}";
    public static final String JOB_SPARE_PARTS_FOR_MAINTENANCE = "pms/jobs/get_parts_maintenance/{maintenanceId}";
    public static final String INVENTORY_SPARE_PART = "inventory/spare_part/get/{spare_part_code}";
    public static final String INVENTORY_SPARE_PART_FOR_MAINTENANCE = "inventory/spare_part/maintenance_get/{maintenanceId}";
    public static final String JOB_START = "pms/jobs/start/{code}";
    public static final String JOB_COMPLETE = "pms/jobs/complete/{code}";
    public static final String UPDATE_SPARE_PART_MAINTENANCE_QUANTITY = "pms/jobs/update/spare_part_needed_quantity";


    public static final String INVENTORY_FETCH_MACHINES = "inventory/machine/get/{floor}";
    public static final String INVENTORY_FETCH_LOCATION_PACKETS = "inventory/fetch_location_packets";
    public static final String INVENTORY_ADD_BOX = "inventory/add_box";
    public static final String INVENTORY_MACHINE_LOCATIONS = "inventory/machine/get_locations/{model}";
    public static final String INVENTORY_LINK_NEW_LOCATIONS_MACHINE = "location/add/link_machine";
    public static final String INVENTORY_UPDATE_LOCATION_MACHINE = "location/update/link_machine";
    public static final String INVENTORY_FETCH_ROOMS = "location/get_rooms/{floor}";
    //    /location/get_packets/:location
    public static final String INVENTORY_FETCH_LOCATION_PACKETS_BY_LOCATION = "location/get_packets/{location}/{machine}";
    public static final String ASSIGN_PACKET = "inventory/packet/assign";
    public static final String INVENTORY_UPDATE_PACKET = "inventory/packet/update";
    public static final String INVENTORY_FETCH_MACHINE_PARTS = "inventory/machine/get_parts/{machine}";
    public static final String UPDATE_PACKET_FOR_IN_USE = "inventory/packet/update_in_use/{tagId}";
    public static final String INVENTORY_FETCH_MACHINE_INFO = "inventory/machine/info/{model}";
    public static final String GET_SPARE_PART_ROB = "inventory/spare_part/get_rob/{spare_part_code}";
    public static final String TEST_CONNECTION = "ack";
}
