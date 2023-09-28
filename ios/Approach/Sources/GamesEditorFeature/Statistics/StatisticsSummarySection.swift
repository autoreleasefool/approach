import AssetsLibrary
import StringsLibrary
import SwiftUI

public struct StatisticsSummarySection: View {
	let currentGameIndex: Int
	let onTapSeries: () -> Void
	let onTapGame: () -> Void

	public var body: some View {
		Section {
			Grid {
				GridRow {
					Image(systemSymbol: .chartLineUptrendXyaxis)
						.resizable()
						.scaledToFit()
						.frame(width: .smallIcon, height: .smallIcon)
						.padding()
//						.background(
//							RoundedRectangle(cornerRadius: .standardRadius)
//								.fill(Color(uiColor: .secondarySystemGroupedBackground))
//						)

					Label(
						title: Strings.Series.title,
						subtitle: Strings.Game.Editor.Fields.Statistics.viewStatistics,
						onPress: onTapSeries
					)

					Label(
						title: Strings.Game.titleWithOrdinal(currentGameIndex + 1),
						subtitle: Strings.Game.Editor.Fields.Statistics.viewStatistics,
						onPress: onTapGame
					)
				}
			}
			.listRowInsets(EdgeInsets())
		}
		.listRowBackground(Color.clear)
	}
}

struct Label: View {
	let title: String
	let subtitle: String
	let onPress: () -> Void

	fileprivate init(title: String, subtitle: String, onPress: @escaping () -> Void) {
		self.title = title
		self.subtitle = subtitle
		self.onPress = onPress
	}

	public var body: some View {
		NavigationButton(action: onPress) {
			VStack(alignment: .leading) {
				Text(title)
					.font(.subheadline)
					.fontWeight(.bold)
					.frame(maxWidth: .infinity, alignment: .leading)
				Text(subtitle)
					.font(.caption2)
					.frame(maxWidth: .infinity, alignment: .leading)
			}
		}
		.padding(.smallSpacing)
		.frame(maxHeight: .infinity)
		.background(
			RoundedRectangle(cornerRadius: .standardRadius)
				.fill(Color(uiColor: .secondarySystemGroupedBackground))
		)
	}
}

#if DEBUG
struct StatisticsSummarySectionPreview: PreviewProvider {
	static var previews: some View {
		Form {
			StatisticsSummarySection(currentGameIndex: 0, onTapSeries: {}, onTapGame: {})
		}
	}
}
#endif
