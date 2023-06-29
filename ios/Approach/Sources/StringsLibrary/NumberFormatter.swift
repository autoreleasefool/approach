import Foundation

private let emptyValue = "—"

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

public func format(percentage: Double?) -> String {
	guard let percentage else { return emptyValue }
	return percentageFormatter.string(for: percentage) ?? emptyValue
}

public func format(percentageWithModifier: Double?) -> String {
	guard let percentageWithModifier else { return emptyValue }
	let formatted = format(percentage: percentageWithModifier)
	if formatted == "0%" || formatted == emptyValue {
		return formatted
	} else if percentageWithModifier > 0 {
		return "+\(formatted)"
	} else {
		return "-\(formatted)"
	}
}
