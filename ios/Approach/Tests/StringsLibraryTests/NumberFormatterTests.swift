@testable import StringsLibrary
import Testing

struct NumberFormatterTests {
	@Test func testAverageFormat() {
		#expect(format(average: nil) == "—")
		#expect(format(average: 123.0) == "123")
		#expect(format(average: 99.9999) == "100")
		#expect(format(average: 206.52342) == "206.5")
	}

	@Test func testPercentFormat() {
		#expect(format(percentage: nil) == "—")
		#expect(format(percentage: 0.86) == "86%")
		#expect(format(percentage: 0.38432) == "38.4%")
		#expect(format(percentage: 0.99999) == "100%")
	}

	@Test func testPercentFormat_WithNumerator_WithoutDenominator() {
		#expect(format(percentage: nil, withNumerator: 1) == "—")
		#expect(format(percentage: 0.86, withNumerator: 1) == "86%")
		#expect(format(percentage: 0.38432, withNumerator: 1) == "38.4%")
		#expect(format(percentage: 0.99999, withNumerator: 1) == "100%")
	}

	@Test func testPercentFormat_WithoutNumerator_WithDenominator() {
		#expect(format(percentage: nil, withDenominator: 1) == "—")
		#expect(format(percentage: 0.86, withDenominator: 1) == "86%")
		#expect(format(percentage: 0.38432, withDenominator: 1) == "38.4%")
		#expect(format(percentage: 0.99999, withDenominator: 1) == "100%")
	}

	@Test func testPercentFormat_WithNumerator_WithDenominator() {
		#expect(format(percentage: nil, withNumerator: 1, withDenominator: 2) == "—")
		#expect(format(percentage: 0.86, withNumerator: 1, withDenominator: 2) == "86% (1/2)")
		#expect(format(percentage: 0.38432, withNumerator: 1, withDenominator: 2) == "38.4% (1/2)")
		#expect(format(percentage: 0.99999, withNumerator: 1, withDenominator: 2) == "100% (1/2)")
	}
}
