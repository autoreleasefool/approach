import StatisticsLibrary
import StringsLibrary
import XCTest

func AssertCounting<T>(
	_ statistic: T,
	equals count: Int,
	file: StaticString = #file,
	line: UInt = #line
) where T: Statistic & CountingStatistic {
	XCTAssertEqual(statistic.count, count, file: file, line: line)
	XCTAssertEqual(statistic.formattedValue, String(count), file: file, line: line)
	XCTAssertEqual(statistic.isEmpty, count == 0, file: file, line: line)
}

func AssertHighestOf<T>(
	_ statistic: T,
	equals highest: Int,
	file: StaticString = #file,
	line: UInt = #line
) where T: Statistic & HighestOfStatistic {
	XCTAssertEqual(statistic.highest, highest, "Highest does not match", file: file, line: line)
	XCTAssertEqual(statistic.formattedValue, String(highest), "Formatted value does not match", file: file, line: line)
	XCTAssertEqual(statistic.isEmpty, highest == 0, "isEmpty does not match", file: file, line: line)
}

func AssertAveraging<T>(
	_ statistic: T,
	hasTotal total: Int,
	withDivisor divisor: Int,
	formattedAs formattedValue: String,
	file: StaticString = #file,
	line: UInt = #line
) where T: Statistic & AveragingStatistic {
	let average = divisor > 0 ? Double(total) / Double(divisor) : 0
	XCTAssertEqual(statistic.total, total, "Total does not match", file: file, line: line)
	XCTAssertEqual(statistic.divisor, divisor, "Divisor does not match", file: file, line: line)
	XCTAssertEqual(statistic.isEmpty, divisor == 0, "isEmpty does not match", file: file, line: line)
	XCTAssertEqual(statistic.average, average, "Average does not match", file: file, line: line)
	XCTAssertEqual(statistic.formattedValue, formattedValue, "Formatted value does not match", file: file, line: line)
}

func AssertPercentage<T>(
	_ statistic: T,
	hasNumerator numerator: Int,
	withDenominator denominator: Int,
	formattedAs formattedValue: String,
	file: StaticString = #file,
	line: UInt = #line
) where T: Statistic & PercentageStatistic {
	let percentage = denominator > 0 ? Double(numerator) / Double(denominator) : 0
	XCTAssertEqual(statistic.numerator, numerator, "Numerator does not match", file: file, line: line)
	XCTAssertEqual(statistic.denominator, denominator, "Denominator does not match", file: file, line: line)
	XCTAssertEqual(statistic.isEmpty, denominator == 0, "isEmpty does not match", file: file, line: line)
	XCTAssertEqual(statistic.percentage, percentage, "Percentage does not match", file: file, line: line)
	XCTAssertEqual(statistic.formattedValue, formattedValue, "Formatted value does not match", file: file, line: line)
}
