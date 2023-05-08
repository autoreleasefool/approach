import AssetsLibrary
import SwiftUI
import ViewsLibrary

public struct GameSummaryHeader: View {
	let bowlerName: String
	let leagueName: String
	let accessory: Accessory
	let onTapAccessory: () -> Void

	public var body: some View {
		HStack(alignment: .top) {
			VStack(alignment: .leading) {
				Text(bowlerName)
					.font(.headline)
					.frame(maxWidth: .infinity, alignment: .leading)
				Text(leagueName)
					.font(.subheadline)
					.frame(maxWidth: .infinity, alignment: .leading)
			}

			Spacer()

			switch accessory {
			case let .nextBowler(nextBowler):
				Button(action: onTapAccessory) {
					HStack {
						Text(nextBowler)
						Image(systemName: "chevron.forward")
							.resizable()
							.scaledToFit()
							.frame(width: .tinyIcon, height: .tinyIcon)
					}
				}
				.contentShape(Rectangle())
				.buttonStyle(TappableElement())

			case let .seriesDate(date):
				Text(date)
					.font(.caption)
			}
		}
		.listRowInsets(EdgeInsets())
		.listRowBackground(Color.clear)
	}
}

extension GameSummaryHeader {
	enum Accessory {
		case nextBowler(String)
		case seriesDate(String)
	}
}
