package com.example.backend.region;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegionService {
    @Autowired
    private RegionRepository regionRepository;

    public List<Region> getRegionsByUserId(Long userId) {
        return regionRepository.findByUserId(userId);
    }

    public Region saveRegion(Region region) {
        return regionRepository.save(region);
    }

    public Region getRegionById(Long id) {
        return regionRepository.findById(id).orElse(null);
    }

    public void deleteRegion(Long id) {
        regionRepository.deleteById(id);
    }
}
