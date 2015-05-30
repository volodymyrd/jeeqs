package com.gmail.volodymyrdotsenko.jee.batch.web;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/")
public class BatchController {
	@POST
    @Path("hello")
    //@Produces({ "application/xml" })
    public String getHelloWorld() {
        return "hello world!";
    }
}