package com.voly_saina.service;

import java.util.Map;
import java.util.HashMap;
import org.springframework.stereotype.Service;

@Service
public class ProduitImageService {

    private static final String DEFAULT_IMAGE = "/img/produits/default.png";
    private static final String IMAGES_BASE = "/img/produits/";

    private static final Map<Long, String> ID_TO_IMAGE = new HashMap<>();

    static {
        ID_TO_IMAGE.put(1L, IMAGES_BASE + "npk.jpg");
        ID_TO_IMAGE.put(2L, IMAGES_BASE + "uree.png");
        ID_TO_IMAGE.put(3L, IMAGES_BASE + "compost.png");
        ID_TO_IMAGE.put(4L, IMAGES_BASE + "huile-moteure.jpg");
        ID_TO_IMAGE.put(5L, IMAGES_BASE + "semence-katsaka.png");
    }

    public String getImagePath(Long idProduit) {
        return ID_TO_IMAGE.getOrDefault(idProduit, DEFAULT_IMAGE);
    }

    public Map<Long, String> getAllImages() {
        return new HashMap<>(ID_TO_IMAGE);
    }
}
