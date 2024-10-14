package com.filmfellows.cinemates.app.productadmin;

import com.filmfellows.cinemates.domain.productadmin.model.service.ProductAdminService;
import com.filmfellows.cinemates.domain.productadmin.model.vo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ProductadminController {

    private ProductAdminService apService;
    @Autowired
    public ProductadminController(ProductAdminService apService) {
        this.apService=apService;
    }


    @GetMapping("/product/admin")
    public String showproductadmin(Model model) {
        List<Product> pList=apService.allproduct();
        model.addAttribute("pList", pList);
        return "pages/productadmin/productadmin";
    }
    @GetMapping("/product/adminupdate{productNo}")
    public String showproductadminupdate(@PathVariable("productNo") int productNo, Model model) {
        Product product=apService.oneproduct(productNo);
        model.addAttribute("product", product);
        return "pages/productadmin/updateproduct";

    }
    @PostMapping("/product/adminupdate")
    public String adminupdateProduct(Product product) {
        System.out.println(product);
        int result=apService.updateproduct(product);
        if(result>0) {
            return "pages/cinema/admin/sucess";
        }else{
            return "pages/cinema/admin/failed";
        }
    }
    @GetMapping("/product/admininsert")
    public String showproductadmininsert(Model model) {
        return "pages/productadmin/insertproduct";
    }
    @PostMapping("/product/admininsert")
    public String admininsertProduct(Product product) {
        int result=apService.insertproduct(product);
        if(result>0) {
            return "pages/cinema/admin/sucess";
        }else{
            return "pages/cinema/admin/failed";
        }
    }
    @GetMapping("/product/admindelete{productNo}")
    public String productadmindelete(@PathVariable("productNo") int productNo) {
        int result=apService.deleteproduct(productNo);
        if(result>0) {
            return "pages/cinema/admin/sucess";
        }else{
            return "pages/cinema/admin/failed";
        }
    }
}
