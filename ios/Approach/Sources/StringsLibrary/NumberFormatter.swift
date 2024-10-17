import Foundation

private let emptyValue = "—"

public let fileSizeFormatter: NumberFormatter = {
	let formatter = NumberFormatter()
	formatter.maximumFractionDigits = 1
	formatter.alwaysShowsDecimalSeparator = true
	return formatter
}()

public func formatAsMb(fileSizeBytes: Int) -> String {
	let mb = Double(fileSizeBytes) / 1_000_000.0
	return fileSizeFormatter.string(for: mb) ?? emptyValue
}

public let averageFormatter: NumberFormatter = {
	let formatter = NumberFormatter()
	formatter.maximumFractionDigits = 1
	formatter.alwaysShowsDecimalSeparator = false
	return formatter
}()

public func format(average: Double?) -> String {
	guard let average, average > 0 else { return emptyValue }
	return averageFormatter.string(for: average) ?? emptyValue
}

public let percentageFormatter: NumberFormatter = {
	let formatter = NumberFormatter()
	formatter.numberStyle = .percent
	formatter.maximumFractionDigits = 1
	formatter.alwaysShowsDecimalSeparator = false
	return formatter
}()

public func format(
	percentage: Double?,
	withNumerator: Int? = nil,
	withDenominator: Int? = nil
) -> String {
	guard let percentage else { return emptyValue }
	if let withNumerator, let withDenominator, let percentage = percentageFormatter.string(for: percentage) {
		return "\(percentage) (\(withNumerator)/\(withDenominator))"
	} else {
		return percentageFormatter.string(for: percentage) ?? emptyValue
	}
}

public func format(percentageWithModifier: Double?) -> String {
	guard let percentageWithModifier else { return emptyValue }
	let formatted = format(percentage: percentageWithModifier)
	if formatted == "0%" || formatted == emptyValue || percentageWithModifier < 0 {
		return formatted
	} else {
		return "+\(formatted)"
	}
}
