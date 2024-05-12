package com.test.dummy.controllers;
import java.io.InputStream;
import java.nio.file.*;
import java.sql.Date;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.test.dummy.models.Products;
import com.test.dummy.models.ProductsDto;
import com.test.dummy.services.ProductsRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping ("/products")

public class ProductsController {
	
	@Autowired
		private ProductsRepository repo;
	
	@GetMapping({""," / "})
	public String showProductList(Model model) {
		List<Products>products =repo.findAll(Sort.by(Sort.Direction.DESC,"id"));
		model.addAttribute("products",products);
		return"products/index";
	}
 
	@GetMapping("/create ")
	  public String showCreatePage(Model model) {
		ProductsDto productDto=new ProductsDto();
		model.addAttribute("productDto",productDto);
		return"product/CreateProduct";
	}
	
	      @PostMapping("/create")
	      public String createProduct(
	    		  @Valid @ModelAttribute Products productDto,
	    		  BindingResult result
	    		  ) {
	    	  if (productDto.getImageFileName().isEmpty()) {
	    		  result.addError(new FieldError("productDto","imageFile","the Image file is required"));
	    	  }
	    	  if(result.hasErrors()){
	    		  return "products/CreateProduct";
	    	  }
	    	  
	    	  
	    	  
	    	  
	    	  
	    	  
	    	  //to save an image 
	    	  
	         MultipartFile image = productDto.getImageFileName();
                Date createdAt = new Date(0);
               String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();
               
                   try {
                	   String uploadDir = "public/dummyImages/";
                	   Path uploadPath=  Paths.get(uploadDir);
	    	
                	   if (!Files.exists (uploadPath)) {
                		   Files.createDirectories (uploadPath);
                	   }
                	   
                      try (InputStream inputStream = image.getInputStream()) {
                    	  Files.copy(inputStream, Paths.get(uploadDir+ storageFileName),
                    	   StandardCopyOption.REPLACE_EXISTING);
                      }
                   } catch (Exception ex) {
                	   System.out.println("Exception: " + ex.getMessage());
                   }	
                    
                   Products product=new Products();
                   product.setName(productDto.getName());
                   product.setBrand(productDto.getBrand());
                   product.setCategory(productDto.getCategory());
                   product.setPrice(productDto.getPrice());
                   product.setDescription(productDto.getDescription());
                   product.setCreateAt(createdAt);
                  product.setImageFileName(storageFileName);
                   
                   repo.save(product);
                   
                   return "redirect:/products";	 
                   
	      }
	      @GetMapping("/edit")
	      public String showEditPage(
	    		  Model model,
	    		  @RequestParam int id
	    		  ) {
	    	  try {
	    		  Products product =repo.findById(id).get();
	    		  model.addAttribute("product",product);
	    		  
	    		  
                  ProductsDto productDto =new ProductsDto();
                  product.setName(productDto.getName());
                  product.setBrand(productDto.getBrand());
                  product.setCategory(productDto.getCategory());
                  product.setPrice(productDto.getPrice());
                  product.setDescription(productDto.getDescription());
                  
                  model.addAttribute("productDto",productDto);
	    		  
	    	  } catch (Exception ex) {
           	   System.out.println("Exception: " + ex.getMessage());
	    	  }
           	 return "redirect:/product";
	    	  }
	      
           	 @PostMapping("/edit")
   	      public String updateProduct(
   	    		  Model model,
   	    		  @RequestParam int id,
   	    		  @Valid @ModelAttribute ProductsDto productDto,
   	    		 
   	    		  BindingResult result
   	    		  ) {
           		 
           		 
           		 try {
           			 Products product =repo.findById(id).get();
           			 model.addAttribute("product",product);
           			 if(result.hasErrors()) {
           				 return"products/EditProduct";
           			 }
           			 
           			 if(!productDto.getImageFile().isEmpty()) {
           				 //delete old image
           				 String uploadDir="public/dummyImages/";
           				 Path oldImagespath= Paths.get(uploadDir+product.getImageFileName());
           				 
           				 try {
           					 Files.delete(oldImagespath);
           					 
           				 }
           				catch(Exception ex) {
                      		 System.out.println("Exception: " + ex.getMessage());
                      		 
           			 }
           			 // to save a new file Image
           				 MultipartFile image = productDto.getImageFile();
                         Date createdAt = new Date(0);
                        String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();
                        
                         
                         	   
                               try (InputStream inputStream = image.getInputStream()) {
                             	  Files.copy(inputStream, Paths.get(uploadDir+ storageFileName),
                             	   StandardCopyOption.REPLACE_EXISTING);
                               }
                               
						product.setImageFileName(storageFileName);
                               
                            }
           			 
           			 
           			 product.setName(productDto.getName());
                     product.setBrand(productDto.getBrand());
                     product.setCategory(productDto.getCategory());
                     product.setPrice(productDto.getPrice());
                     product.setDescription(productDto.getDescription());
                repo.save(product);
           			 
           		 }
           		 catch (Exception ex)
           		 {
                         	   System.out.println("Exception: " + ex.getMessage());
                         	  return "redirect:/products";
           		 }
           		return"products/EditProduct";
           	 }
           	 
           		
           	 
           		 
           	 
	     
           	 @GetMapping("/delete")
           	 public String deleteProduct( @RequestParam int id)
                 	
                   	 
           	
           	 {
           		 try 
           		 {
   					 Products product =repo.findById(id).get();
   					 //delete product image 
   					 Path imagePath=Paths.get("public/dummyImages/"+ product.getImageFileName());
   					try {
						 Files.delete(imagePath);
					 }
					catch(Exception ex) {
           		 System.out.println("Exception: " + ex.getMessage());

					}
           		 }
           		 
				catch(Exception ex)
           		 {
           		 System.out.println("Exception: " + ex.getMessage());

				}
           		 
            		return "redirect:/products";
    			 }
            	 
 }	
            		 
    				
      	 
   										 
   					 
   				            		 
           	
           		 
	      

           	 
	      
       

           			 
           			 
           			 
           			 
           		
           			 
           			 
           		 
           		 
           		 
           	 
           

                		

                	
	    	 

	    	 
	    	 

	    	 

	    	  

	    	  
	    	  
	    	  
	    	  
	    	  
	    	  
	    	  
	    	  
	    	  
	    	  
	    	  
	    	  
	    	  
	    	  
	    	  
	    	  
	    	  
	    	  
	    	  
	    	   
	    	
	      

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

