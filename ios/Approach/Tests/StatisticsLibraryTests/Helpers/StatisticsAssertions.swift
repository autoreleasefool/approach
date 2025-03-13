import StatisticsLibrary
import StringsLibrary
import Testing
import XCTest

// TODO: enable deprecation warning
// @available(*, deprecated, renamed: "expectCounting")
func AssertCounting<T>(
	_ statistic: T,
	equals count: Int,
	file: StaticString = #filePath,
	line: UInt = #line
) where T: Statistic & CountingStatistic {
	XCTAssertEqual(statistic.count, count, file: file, line: line)
	XCTAssertEqual(statistic.formattedValue, String(count), file: file, line: line)
	// We want to assert `isEmpty` to be when `count` is zero, so we can't use `isEmpty` here as that's under test
	// swiftlint:disable:next empty_count
	XCTAssertEqual(statistic.isEmpty, count == 0, file: file, line: line)
}

func expectCounting<T>(
	_ statistic: T,
	equals count: Int,
	sourceLocation: SourceLocation = #_sourceLocation
) where T: Statistic & CountingStatistic {
	#expect(statistic.count == count, sourceLocation: sourceLocation)
	#expect(statistic.formattedValue == String(count), sourceLocation: sourceLocation)
	// We want to assert `isEmpty` to be when `count` is zero, so we can't use `isEmpty` here as that's under test
	// swiftlint:disable:next empty_count
	#expect(statistic.isEmpty == (count == 0), sourceLocation: sourceLocation)
}

// TODO: enable deprecation warning
// @available(*, deprecated, renamed: "expectHighestOf")
func AssertHighestOf<T>(
	_ statistic: T,
	equals highest: Int,
	file: StaticString = #filePath,
	line: UInt = #line
) where T: Statistic & HighestOfStatistic {
	XCTAssertEqual(statistic.highest, highest, "Highest does not match", file: file, line: line)
	XCTAssertEqual(statistic.formattedValue, String(highest), "Formatted value does not match", file: file, line: line)
	XCTAssertEqual(statistic.isEmpty, highest == 0, "isEmpty does not match", file: file, line: line)
}

func expectHighestOf<T>(
	_ statistic: T,
	equals highest: Int,
	sourceLocation: SourceLocation = #_sourceLocation
) where T: Statistic & HighestOfStatistic {
	#expect(statistic.highest == highest, sourceLocation: sourceLocation)
	#expect(statistic.formattedValue == String(highest), sourceLocation: sourceLocation)
	#expect(statistic.isEmpty == (highest == 0), sourceLocation: sourceLocation)
}

// TODO: enable deprecation warning
// @available(*, deprecated, renamed: "expectAveraging")
func AssertAveraging<T>(
	_ statistic: T,
	hasTotal total: Int,
	withDivisor divisor: Int,
	formattedAs formattedValue: String,
	file: StaticString = #filePath,
	line: UInt = #line
) where T: Statistic & AveragingStatistic {
	let average = divisor > 0 ? Double(total) / Double(divisor) : 0
	XCTAssertEqual(statistic.total, total, "Total does not match", file: file, line: line)
	XCTAssertEqual(statistic.divisor, divisor, "Divisor does not match", file: file, line: line)
	XCTAssertEqual(statistic.isEmpty, divisor == 0, "isEmpty does not match", file: file, line: line)
	XCTAssertEqual(statistic.average, average, "Average does not match", file: file, line: line)
	XCTAssertEqual(statistic.formattedValue, formattedValue, "Formatted value does not match", file: file, line: line)
}

func expectAveraging<T>(
	_ statistic: T,
	hasTotal total: Int,
	withDivisor divisor: Int,
	formattedAs formattedValue: String,
	sourceLocation: SourceLocation = #_sourceLocation
) where T: Statistic & AveragingStatistic {
	let average = divisor > 0 ? Double(total) / Double(divisor) : 0
	#expect(statistic.total == total, sourceLocation: sourceLocation)
	#expect(statistic.divisor == divisor, sourceLocation: sourceLocation)
	#expect(statistic.isEmpty == (divisor == 0), sourceLocation: sourceLocation)
	#expect(statistic.average == average, sourceLocation: sourceLocation)
	#expect(statistic.formattedValue == formattedValue, sourceLocation: sourceLocation)
}

// TODO: enable deprecation warning
// @available(*, deprecated, renamed: "expectPercentage")
func AssertPercentage<T>(
	_ statistic: T,
	hasNumerator numerator: Int,
	withDenominator denominator: Int,
	formattedAs formattedValue: String,
	overridingIsEmptyExpectation: Bool? = nil,
	file: StaticString = #filePath,
	line: UInt = #line
) where T: Statistic & PercentageStatistic {
	let percentage = denominator > 0 ? Double(numerator) / Double(denominator) : 0
	XCTAssertEqual(statistic.numerator, numerator, "Numerator does not match", file: file, line: line)
	XCTAssertEqual(statistic.denominator, denominator, "Denominator does not match", file: file, line: line)
	XCTAssertEqual(statistic.isEmpty, overridingIsEmptyExpectation == nil ? denominator == 0 : overridingIsEmptyExpectation, "isEmpty does not match", file: file, line: line)
	XCTAssertEqual(statistic.percentage, percentage, "Percentage does not match", file: file, line: line)
	XCTAssertEqual(statistic.formattedValue, formattedValue, "Formatted value does not match", file: file, line: line)
}

func expectPercentage<T>(
	_ statistic: T,
	hasNumerator numerator: Int,
	withDenominator denominator: Int,
	formattedAs formattedValue: String,
	overridingIsEmptyExpectation: Bool? = nil,
	sourceLocation: SourceLocation = #_sourceLocation
) where T: Statistic & PercentageStatistic {
	let percentage = denominator > 0 ? Double(numerator) / Double(denominator) : 0
	#expect(statistic.numerator == numerator, sourceLocation: sourceLocation)
	#expect(statistic.denominator == denominator, sourceLocation: sourceLocation)
	#expect(statistic.isEmpty == (overridingIsEmptyExpectation ?? (denominator == 0)), sourceLocation: sourceLocation)
	#expect(statistic.percentage == percentage, sourceLocation: sourceLocation)
	#expect(statistic.formattedValue == formattedValue, sourceLocation: sourceLocation)
}
