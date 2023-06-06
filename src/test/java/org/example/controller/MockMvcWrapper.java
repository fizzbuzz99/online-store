package org.example.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.deals.DealType;
import org.example.model.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


public class MockMvcWrapper {

    public static final ObjectMapper jsonObjectMapper = new ObjectMapper();

    private final MockMvc mvc;

    public MockMvcWrapper(MockMvc mvc) {
        this.mvc = mvc;
    }

    public StoreUser addNewUser(String name) throws Exception {
        StoreUser user = StoreUser.builder().name(name).build();
        String json = jsonObjectMapper.writeValueAsString(user);
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/admin/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(equalTo(name)))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        return jsonObjectMapper.readValue(content, StoreUser.class);
    }

    public MvcResult getUserValidateIdName(StoreUser user) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.get("/admin/user/" + user.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(user.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(user.getName()))
                .andReturn();
    }

    public MvcResult getAllUsers() throws Exception {
        return mvc.perform(MockMvcRequestBuilders.get("/admin/user/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    public MvcResult getUser(StoreUser user) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.get("/admin/user/" + user.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    public MvcResult removeUserById(Long id) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.delete("/admin/user/remove/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    public Deal addNewDeal(DealType dealType) throws Exception {

        Deal deal = Deal.builder()
                .name(dealType.name())
                .description(dealType.name())
                .type(dealType)
                .build();

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/admin/product/deal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObjectMapper.writeValueAsString(deal))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(equalTo(deal.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type").value(equalTo(dealType.name())))
                .andReturn();
        return jsonObjectMapper.readValue(result.getResponse().getContentAsString(), Deal.class);
    }

    public MvcResult addItemToBasket(BasketItem basketItem, Long userId) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.post("/store/user/"+userId+"/item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObjectMapper.writeValueAsString(basketItem))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                //.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(notNullValue()))
                .andReturn();
    }

    public MvcResult getReceipt(Long userId) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.get("/store/user/"+userId+"/receipt")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                //.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(notNullValue()))
                .andReturn();
    }

    public MvcResult getDeal(Deal deal) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.get("/admin/product/deal/" + deal.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(deal.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(equalTo(deal.getName())))
                .andReturn();
    }

    public void removeDalById(Long id) throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/admin/product/deal/remove/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    public MvcResult addNewProduct(Product product) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.post("/admin/product/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObjectMapper.writeValueAsString(product)).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(equalTo(product.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(equalTo(product.getPrice())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.inventory").value(equalTo(product.getInventory())))
                .andReturn();
    }

    public MvcResult getProductById(Long id) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.get("/admin/product/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    public Product convertToProduct(MvcResult result) throws Exception {
        return jsonObjectMapper.readValue(result.getResponse().getContentAsString(), Product.class);
    }

    public StoreUser convertToUser(MvcResult result) throws Exception {
        return jsonObjectMapper.readValue(result.getResponse().getContentAsString(), StoreUser.class);
    }

    public Deal convertToDeal(MvcResult result) throws Exception {
        return jsonObjectMapper.readValue(result.getResponse().getContentAsString(), Deal.class);
    }

    public BasketItem convertToBasketItem(MvcResult result) throws Exception {
        return jsonObjectMapper.readValue(result.getResponse().getContentAsString(), BasketItem.class);
    }

    public Receipt convertToReceipt(MvcResult result) throws Exception {
        return jsonObjectMapper.readValue(result.getResponse().getContentAsString(), Receipt.class);
    }

    public List<StoreUser> convertToListOfUsers(MvcResult result) throws Exception {
        return jsonObjectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {});
    }
}
