package com.skyrate.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.skyrate.clib.model.SuccessIDResponse;
import com.skyrate.model.citystate.CountryList;
import com.skyrate.model.citystate.CountryListResponse;
import com.skyrate.model.citystate.CountryResponse;
import com.skyrate.model.citystate.StateResponse;
import com.skyrate.model.citystate.UploadCountry;
import com.skyrate.model.dbentity.AddressState;
import com.skyrate.model.dbentity.Country;
import com.skyrate.service.CityStateService;

@RestController
@RequestMapping("/address")
@CrossOrigin(maxAge=3600)
public class CityStateController {

private static final Logger logger = LoggerFactory.getLogger(CityStateController.class);
	
	@Autowired
	private CityStateService cityStateService;
	
	@RequestMapping(method = RequestMethod.GET, value = "/getStates")
	public StateResponse getStates(){
		StateResponse response = new StateResponse();
		try{
			List<AddressState> getStates = cityStateService.getState();
			response.setStates(getStates);
		}
		catch(Exception e){
			logger.error("Getting States failed",e);
			response.setSuccess(false);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/getCountryList")
	public CountryListResponse getCountryList(){
		CountryListResponse response = new CountryListResponse();
		try{
			List<CountryList> country = cityStateService.getCountryList();
			response.setCountry(country);
			logger.info("get Country");
		}
		catch(Exception e){
			logger.error("failes",e);
			response.setSuccess(false);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/getCountry")
	public CountryResponse getCountry(){
		CountryResponse response = new CountryResponse();
		try{
			List<Country> country = cityStateService.getCountry();
			response.setCountry(country);
			logger.info("get Country");
		}
		catch(Exception e){
			logger.error("failes",e);
			response.setSuccess(false);
		}
		return response;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/uploadCountry")
	public SuccessIDResponse uploadCountry(@RequestBody List<Country> request){
		SuccessIDResponse response = new SuccessIDResponse();
		try{
			for(Country countryGet : request){
				Country country = new Country();
				country.setName(countryGet.getName());
				country.setCode(countryGet.getCode());
				cityStateService.saveCountry(country);
			}
		}
		catch(Exception e){
			logger.error("error country",e);
		}
		return response;
	}
}
