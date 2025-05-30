import AssetsLibrary
import MatchPlaysRepositoryInterface
import ModelsLibrary
import StringsLibrary
import SwiftUI

public struct MatchPlaySummarySection: View {
	let matchPlay: MatchPlay.Edit?
	let action: () -> Void

	public var body: some View {
		Section(Strings.MatchPlay.title) {
			NavigationButton(action: action) {
				HStack(spacing: .standardSpacing) {
					Image(systemName: matchPlay?.opponent == nil ? "person.2.slash" : "person.fill")
						.resizable()
						.scaledToFit()
						.frame(width: .smallIcon, height: .smallIcon)

					VStack(alignment: .leading) {
						Text(matchPlay?.opponent?.name ?? Strings.MatchPlay.Summary.Opponent.none)
							.font(matchPlay?.opponent == nil ? .title3 : .title2)
							.fontWeight(matchPlay?.opponent == nil ? .regular : .heavy)
							.italic(matchPlay?.opponent == nil)
							.frame(maxWidth: .infinity, alignment: .leading)

						Text(opponentScore)
							.font(.subheadline)
					}

					Spacer()

					Image(systemName: matchPlay?.result?.systemImage ?? "square.slash")
						.resizable()
						.scaledToFit()
						.foregroundStyle(matchPlay?.result?.foregroundStyle ?? Asset.Colors.Text.onBackground.swiftUIColor)
						.frame(width: .smallerIcon, height: .smallerIcon)
				}
			}
		}
	}

	private var opponentScore: String {
		if let score = matchPlay?.opponentScore {
			Strings.MatchPlay.Summary.Score.label(score)
		} else {
			Strings.MatchPlay.Summary.Score.none
		}
	}
}

extension MatchPlay.Result {
	var systemImage: String {
		switch self {
		case .lost: "l.square"
		case .won: "w.square"
		case .tied: "t.square"
		}
	}

	var foregroundStyle: Color? {
		switch self {
		case .lost: return Asset.Colors.MatchPlay.lost.swiftUIColor
		case .won: return Asset.Colors.MatchPlay.won.swiftUIColor
		case .tied: return nil
		}
	}
}

#if DEBUG
#Preview {
	Form {
		MatchPlaySummarySection(matchPlay: .init(
			gameId: UUID(0),
			id: UUID(0),
			opponent: .init(id: UUID(0), name: "Joseph"),
			opponentScore: 123,
			result: .lost
		)) {}

		MatchPlaySummarySection(matchPlay: .init(
			gameId: UUID(0),
			id: UUID(0),
			opponent: .init(id: UUID(0), name: "Joseph"),
			opponentScore: 123,
			result: .won
		)) {}

		MatchPlaySummarySection(matchPlay: .init(
			gameId: UUID(0),
			id: UUID(0),
			opponent: .init(id: UUID(0), name: "Joseph"),
			opponentScore: 123,
			result: .tied
		)) {}

		MatchPlaySummarySection(matchPlay: nil) {}
	}
}
#endif
