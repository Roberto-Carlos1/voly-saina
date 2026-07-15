package com.voly_saina.service;

import java.util.Map;
import java.util.HashMap;
import org.springframework.stereotype.Service;

@Service
public class MachineImageService {

    private static final String DEFAULT_IMAGE = "/img/materiels/default.png";
    private static final String IMAGES_BASE = "/img/materiels/";

    private static final Map<Long, String> ID_TO_IMAGE = new HashMap<>();

    static {
        ID_TO_IMAGE.put(1L, IMAGES_BASE + "tracteurStandart.jpeg");
        ID_TO_IMAGE.put(2L, IMAGES_BASE + "motoculteur18cv.avif");
        ID_TO_IMAGE.put(3L, IMAGES_BASE + "pulverisateur.png");
        ID_TO_IMAGE.put(4L, IMAGES_BASE + "remorque.png");
        ID_TO_IMAGE.put(5L, IMAGES_BASE + "tracteur90cv.jpg");
        ID_TO_IMAGE.put(6L, IMAGES_BASE + "motoculteur12cv.jpeg");
    }

    public String getImagePath(Long idMachine) {
        return ID_TO_IMAGE.getOrDefault(idMachine, DEFAULT_IMAGE);
    }

    public Map<Long, String> getAllImages() {
        return new HashMap<>(ID_TO_IMAGE);
    }
}
