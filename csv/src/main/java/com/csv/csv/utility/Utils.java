package com.csv.csv.utility;

import com.csv.csv.domain.VendorVo;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Utils {

    private static ObjectMapper mapper = new ObjectMapper();

    public static<D> D convertArrayToBean(String[] data, String[] fields, Class<D> outClass ) {

        StringBuilder beanStr = new StringBuilder();
        //beanStr.append(" { \"" + fields[0] + "\" : \"" + data[0] + "\"");
        beanStr.append(" { \"" + fields[0] + "\" : \"0\"");
        for(int i = 1; i < fields.length; ++i ) {
            beanStr.append(", \"" + fields[i] + "\" : \"" + data[i] + "\"");
        }
        beanStr.append(" } ");
        try {

            return  mapper.readValue(beanStr.toString(), outClass);
        }
        catch (JsonParseException jpe) {
            System.out.println(jpe.getMessage());
        }
        catch(IOException io) {
            System.out.println(io.getMessage());
        }
        return  null;
    }
}
