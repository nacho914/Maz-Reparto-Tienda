package Retroapi.Model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.xml.transform.Result;

public class POSTSENDNOTIFICATIONS {

    @SerializedName("multicast_id")
    @Expose
    private Integer multicastId;
    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("failure")
    @Expose
    private Integer failure;
    @SerializedName("canonical_ids")
    @Expose
    private Integer canonicalIds;
    @SerializedName("results")
    @Expose
    private List<Retroapi.Model.Result> results = null;

    public Integer getMulticastId() {
        return multicastId;
    }

    public void setMulticastId(Integer multicastId) {
        this.multicastId = multicastId;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public Integer getFailure() {
        return failure;
    }

    public void setFailure(Integer failure) {
        this.failure = failure;
    }

    public Integer getCanonicalIds() {
        return canonicalIds;
    }

    public void setCanonicalIds(Integer canonicalIds) {
        this.canonicalIds = canonicalIds;
    }

    public List<Retroapi.Model.Result> getResults() {
        return results;
    }

    public void setResults(List<Retroapi.Model.Result> results) {
        this.results = results;
    }

}
