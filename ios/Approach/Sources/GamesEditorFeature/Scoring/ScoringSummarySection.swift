import AssetsLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI

public struct ScoringSummarySection: View {
	let scoringMethod: Game.ScoringMethod
	let score: Int
	let action: () -> Void

	public var body: some View {
		Section {
			NavigationButton(action: action) {
				Grid {
					GridRow {
						HStack {
							Image(systemSymbol: scoringMethod.systemSymbol)
								.resizable()
								.scaledToFit()
								.frame(width: .smallIcon, height: .smallIcon)

							Text(String(describing: scoringMethod))
								.fontWeight(.bold)
								.italic()
								.frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .leading)
						}
						.padding()
						.background(
							RoundedRectangle(cornerRadius: .standardRadius)
								.fill(Color(uiColor: .secondarySystemGroupedBackground))
						)

						Text(String(score))
							.font(.title)
							.fontWeight(.black)
							.italic()
							.frame(maxHeight: .infinity, alignment: .center)
							.padding(.horizontal)
							.background(
								RoundedRectangle(cornerRadius: .standardRadius)
									.fill(Color(uiColor: .secondarySystemGroupedBackground))
							)
					}
				}
			}
			.listRowInsets(EdgeInsets())
		} header: {
			Text(Strings.Game.Editor.Fields.ScoringMethod.title)
		} footer: {
			Text(Strings.Game.Editor.Fields.ScoringMethod.help)
		}
		.listRowBackground(Color.clear)
	}
}

extension Game.ScoringMethod: CustomStringConvertible {
	var systemSymbol: SFSymbol {
		switch self {
		case .manual: return .handPointUpLeft
		case .byFrame: return .figureBowling
		}
	}

	public var description: String {
		switch self {
		case .manual: return Strings.Game.Editor.Fields.ScoringMethod.manual
		case .byFrame: return Strings.Game.Editor.Fields.ScoringMethod.byFrame
		}
	}
}

#if DEBUG
struct ScoringSummarySectionPreview: PreviewProvider {
	static var previews: some View {
		Form {
			ScoringSummarySection(scoringMethod: .byFrame, score: 120) {}

			ScoringSummarySection(scoringMethod: .manual, score: 260) {}
		}
	}
}
#endif
