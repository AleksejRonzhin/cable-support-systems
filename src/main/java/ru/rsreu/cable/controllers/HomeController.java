package ru.rsreu.cable.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.rsreu.cable.exceptions.IncorrectFileException;
import ru.rsreu.cable.models.Building;
import ru.rsreu.cable.services.CableSupportSystemService;
import ru.rsreu.cable.utils.BuildingUtils;

import java.util.List;

import static ru.rsreu.cable.utils.FileUtils.*;

@Controller
@RequestMapping("/")
public class HomeController {
    private static final String SUFFIX = "_resolve";
    private static final String EXTENSION = "txt";
    private final CableSupportSystemService service;

    public HomeController(CableSupportSystemService service) {
        this.service = service;
    }


    @GetMapping
    public String showHomePage() {
        return "home";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes attributes) {
        if (file.isEmpty()) {
            attributes.addFlashAttribute("message", "Файл не выбран или пуст");
            return "redirect:/";
        }
        String filename = file.getOriginalFilename();
        List<String> lines = getLinesFromFile(file);
        try {
            Building sourceBuilding = service.createBuilding(lines);
            Building resultBuilding = service.buildCableSupportSystem(sourceBuilding);
            String resultFilename = addSuffixToFilename(filename, SUFFIX, EXTENSION);
            saveResultToFile(BuildingUtils.print(resultBuilding), resultFilename);

            attributes.addFlashAttribute("source", BuildingUtils.printLikeSymbols(sourceBuilding));
            attributes.addFlashAttribute("result", BuildingUtils.printLikeSymbols(resultBuilding));
            attributes.addFlashAttribute("resultFilename",  resultFilename);


            return "redirect:/";
        } catch (IncorrectFileException e) {
            attributes.addFlashAttribute("message", String.format("Содержимое файла некорректно. %s", e.getMessage()));
            return "redirect:/";
        }
    }

    @GetMapping("/load")
    public String loadResult(@RequestParam String resultFilename){

        return "forward:/";
    }
}