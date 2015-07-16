package ftp.cliseau;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.cliseau.runtime.javacor.CriticalEvent;
import net.cliseau.runtime.javacor.DelegationLocPolReturn;
import net.cliseau.runtime.javacor.DelegationReqResp;
import net.cliseau.runtime.javacor.LocalPolicy;
import net.cliseau.runtime.javacor.LocalPolicyResponse;
import net.cliseau.lib.policy.PartitionablePolicy;
import net.cliseau.lib.policy.ChineseWallPolicy.Decision;
import net.cliseau.lib.policy.ChineseWallPolicy.Event;
import net.cliseau.runtime.javacor.EnforcementDecision;
import net.cliseau.runtime.javacor.instlib.DirectDelegationRequest;
import net.cliseau.runtime.javacor.instlib.DirectDelegationResponse;
import net.cliseau.runtime.javacor.instlib.DirectDelegationReqResp;

public class FTP_LocalPolicy extends LocalPolicy {

    int numberOfUnits = 1;

    public FTP_LocalPolicy(String identifier) {
        super(identifier);
        // TODO Auto-generated constructor stub
    }

    @Override
    public LocalPolicyResponse localRequest(CriticalEvent ev)
            throws IllegalArgumentException {
        // TODO Auto-generated method stub
        FTP_CriticalEvent FTP_Event = (FTP_CriticalEvent) ev;
        return makeDecision(FTP_Event);
    }


    @Override
    public LocalPolicyResponse remoteRequest(DelegationReqResp dr) {
        if (dr instanceof DirectDelegationRequest) {
            DirectDelegationRequest req = (DirectDelegationRequest) dr;
            if (getIdentifier().equals(req.getDestID())) {
                // handle request locally and return DirectDelegationResponse
                EnforcementDecision ed = makeDecision(req.getEvent());
                if (getIdentifier().equals(req.getSourceID())) {
                    // the local unit originated the request --> deliver locally
                    return ed;
                } else {
                    // the request originated remotely --> send response back
                    return new DelegationLocPolReturn(req.getSourceID(),
                            new DirectDelegationResponse(ed, getIdentifier(), req.getSourceID()));
                }
            } else {
                // forward request
                return new DelegationLocPolReturn(getNextUnit(req.getDestID()), req);
            }
        } else if (dr instanceof DirectDelegationResponse) {
            DirectDelegationResponse resp = (DirectDelegationResponse) dr;
            if (getIdentifier().equals(resp.getDestID())) {
                // response is for local unit
                // and the response already contains the enforcement decision to return
                return resp.getDecision();
            } else {
                // forward response
                return new DelegationLocPolReturn(getNextUnit(resp.getDestID()), resp);
            }
        } else {
            throw new IllegalArgumentException("Event for remote request of wrong type");
        }
    }


    protected String getResponsibleUnit(CriticalEvent ev) {
        // Note that the "+1" is because the server numbering starts at "1", not "0"
        return "id-1";
        //return "id-" + String.valueOf((Math.abs(ev.hashCode()) % numberOfUnits) + 1);
    }

    protected String getNextUnit(String destinationIdentifier) {
        /*
		String ownId = getIdentifier();
		int destId = Integer.parseInt(ownId.substring(ownId.lastIndexOf('-')+1));
		return "id-" + String.valueOf((destId % numberOfUnits) + 1);
		*/
        return "id-1";
    }

    public EnforcementDecision makeDecision(CriticalEvent ev) {
        FTP_CriticalEvent tev = (FTP_CriticalEvent) ev;
        String fileName = tev.fileName;
        System.out.println("Accessing file: " + fileName + " at unit " + getIdentifier());

        String fileExtension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            fileExtension = fileName.substring(i + 1).toLowerCase();
        }
        int access = -1;

        if (fileExtension.contains("txt") && tev.TXTAccess == 1) {
            access = 1;
        } else if ((fileExtension.contains("png")) && tev.PNGAccess == 1) {
            access = 1;
        } else if ((fileExtension.contains("jpg")) && tev.JPGAccess == 1) {
            access = 1;
        }

        if (fileExtension.contains("txt") && tev.TXTAccess == 0) {
            access = 0;
        } else if ((fileExtension.contains("png")) && tev.PNGAccess == 0) {
            access = 0;
        } else if ((fileExtension.contains("jpg")) && tev.JPGAccess == 0) {
            access = 0;
        }

        if (access == 1) {
            return new FTP_EnforcementDecision(FTP_EnforcementDecision.Value.PERMIT, "file extension permitted!");
        } else if (access == 0){
            return new FTP_EnforcementDecision(FTP_EnforcementDecision.Value.REJECT, "file extension rejected!");
        }else{
            return new FTP_EnforcementDecision(FTP_EnforcementDecision.Value.UNKNOWN,"unknown permission");
        }
    }


}
