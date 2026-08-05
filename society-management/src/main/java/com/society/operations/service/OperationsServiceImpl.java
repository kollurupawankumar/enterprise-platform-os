package com.society.operations.service;

import com.society.operations.entity.AssetEntity;
import com.society.operations.entity.FacilityEntity;
import com.society.operations.entity.SocietyStaffEntity;
import com.society.operations.entity.VendorEntity;
import com.society.operations.repository.AssetRepository;
import com.society.operations.repository.FacilityRepository;
import com.society.operations.repository.SocietyStaffRepository;
import com.society.operations.repository.VendorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OperationsServiceImpl implements OperationsService {

    private final AssetRepository assetRepository;
    private final VendorRepository vendorRepository;
    private final FacilityRepository facilityRepository;
    private final SocietyStaffRepository staffRepository;

    public OperationsServiceImpl(
            AssetRepository assetRepository,
            VendorRepository vendorRepository,
            FacilityRepository facilityRepository,
            SocietyStaffRepository staffRepository) {
        this.assetRepository = assetRepository;
        this.vendorRepository = vendorRepository;
        this.facilityRepository = facilityRepository;
        this.staffRepository = staffRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetEntity> getAllAssets() {
        return assetRepository.findAllWithAmcVendor();
    }

    @Override
    public AssetEntity saveAsset(AssetEntity asset) {
        return assetRepository.save(asset);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VendorEntity> getAllVendors() {
        return vendorRepository.findAll();
    }

    @Override
    public VendorEntity saveVendor(VendorEntity vendor) {
        return vendorRepository.save(vendor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacilityEntity> getAllFacilities() {
        return facilityRepository.findAll();
    }

    @Override
    public FacilityEntity saveFacility(FacilityEntity facility) {
        return facilityRepository.save(facility);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SocietyStaffEntity> getAllStaff() {
        return staffRepository.findAll();
    }

    @Override
    public SocietyStaffEntity saveStaff(SocietyStaffEntity staff) {
        return staffRepository.save(staff);
    }
}
