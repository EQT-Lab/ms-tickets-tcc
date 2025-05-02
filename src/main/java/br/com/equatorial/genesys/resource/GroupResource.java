package br.com.equatorial.genesys.resource;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import br.com.equatorial.genesys.response.dto.GroupResponseDto;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/groups")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Group", description = "Operations related to Group management")
public interface GroupResource {
	
	@GET
    @Operation(summary = "Get all groups", description = "Retrieve a list of all groups.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "List of groups", 
                         content = @Content(mediaType = "application/json", schema = @Schema(implementation = GroupResponseDto.class))),
            @APIResponse(responseCode = "500", description = "Server error")
    })
    public Response listAll();

}
