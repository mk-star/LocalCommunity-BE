package com.example.backend.region;

import com.example.backend.user.User;
import com.example.backend.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController@RequestMapping("/regions")public class RegionController {
    @Autowired
    private RegionService regionService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Region> createRegion(@RequestBody Region region) {
        if (region.getUser() != null && region.getUser().getId() != null) {
            User user = userService.findById(region.getUser().getId());
            if (user != null) {
                region.setUser(user);
                Region createdRegion = regionService.saveRegion(region);
                return new ResponseEntity<>(createdRegion, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Invalid user ID
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // User ID not provided
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Region>> getRegionsByUserId(@PathVariable Long userId) {
        List<Region> regions = regionService.getRegionsByUserId(userId);
        if (regions != null && !regions.isEmpty()) {
            return new ResponseEntity<>(regions, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Region> getRegionById(@PathVariable Long id) {
        Region region = regionService.getRegionById(id);
        if (region != null) {
            return new ResponseEntity<>(region, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegion(@PathVariable Long id) {
        regionService.deleteRegion(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}