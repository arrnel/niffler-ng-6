package guru.qa.niffler.controller;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/internal/spends")
public class SpendController {

    private final SpendService spendService;

    @Autowired
    public SpendController(SpendService spendService) {
        this.spendService = spendService;
    }

    @GetMapping("/{id}")
    public SpendJson getSpend(@PathVariable("id") String id,
                              @RequestParam String username) {
        return spendService.getSpendForUser(id, username);
    }

    @GetMapping("/all")
    public List<SpendJson> getSpends(@RequestParam String username,
                                     @RequestParam(required = false) CurrencyValues filterCurrency,
                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date to) {
        return spendService.getSpendsForUser(username, filterCurrency, from, to);
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public SpendJson addSpend(@RequestBody SpendJson spend) {
        return spendService.saveSpendForUser(spend);
    }

    @PatchMapping("/edit")
    public SpendJson editSpend(@RequestBody SpendJson spend) {
        return spendService.editSpendForUser(spend);
    }

    @DeleteMapping("/remove")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteSpends(@RequestParam String username,
                             @RequestParam List<String> ids) {
        spendService.deleteSpends(username, ids);
    }
}
