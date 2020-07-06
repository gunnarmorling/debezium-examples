package io.debezium.demos.txaggregates.members.rest;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.debezium.demos.txaggregates.members.model.Member;

@Path("/members")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MembersResource {

    @POST
    @Transactional
    public Response createMember(Member member) {
        if (member.id != null) {
            return Response.status(Status.BAD_REQUEST.getStatusCode()).build();
        }

        member.persist();

        return Response.ok(member).status(Status.CREATED).build();
    }

//    @Path("/{id}")
//    @PUT
//    @Transactional
//    public Member updateVegetable(@PathParam("id") long id, Member vegetable) {
//        vegetable.setId(id);
//        vegetable = vegetableService.updateVegetable(vegetable);
//
//        return vegetable;
//    }
//
//    @Path("/{id}")
//    @DELETE
//    @Transactional
//    public void deleteVegetable(@PathParam("id") long id) {
//        vegetableService.deleteVegetable(id);
//    }
}
