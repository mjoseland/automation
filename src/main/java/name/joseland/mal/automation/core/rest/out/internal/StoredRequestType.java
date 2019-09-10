package name.joseland.mal.automation.core.rest.out.internal;

public enum StoredRequestType {
	INTERNAL("Internal", "internal-requests"),
	EXTERNAL("External", "external-requests");

	public final String display;
	public final String resourcePathSegment;

	StoredRequestType(String display, String resourcePathSegment) {
		this.display = display;
		this.resourcePathSegment = resourcePathSegment;
	}
}
