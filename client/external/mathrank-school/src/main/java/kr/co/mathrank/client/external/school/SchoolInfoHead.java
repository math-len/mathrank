package kr.co.mathrank.client.external.school;

public record SchoolInfoHead(
	Integer list_total_count,
	Result RESULT
) {
	public record Result (
		String CODE,
		String MESSAGE
	){
	}
}