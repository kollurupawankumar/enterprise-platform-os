package com.society.operations.service;

import com.society.operations.entity.AssetEntity;
import com.society.operations.entity.FacilityEntity;
import com.society.operations.entity.SocietyStaffEntity;
import com.society.operations.entity.VendorEntity;
import java.util.List;

public interface OperationsService {
    List<AssetEntity> getAllAssets();
    AssetEntity saveAsset(AssetEntity asset);

    List<VendorEntity> getAllVendors();
    VendorEntity saveVendor(VendorEntity vendor);

    List<FacilityEntity> getAllFacilities();
    FacilityEntity saveFacility(FacilityEntity facility);

    List<SocietyStaffEntity> getAllStaff();
    SocietyStaffEntity saveStaff(SocietyStaffEntity staff);
}
