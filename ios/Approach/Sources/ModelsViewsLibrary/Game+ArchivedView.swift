import AssetsLibrary
import DateTimeLibrary
import Foundation
import ModelsLibrary
import StringsLibrary
import SwiftUI

extension Game {
	public struct ArchivedView: SwiftUI.View {
		let score: Int
		let bowlerName: String
		let leagueName: String
		let seriesDate: Date

		public init(score: Int, bowlerName: String, leagueName: String, seriesDate: Date) {
			self.score = score
			self.bowlerName = bowlerName
			self.leagueName = leagueName
			self.seriesDate = seriesDate
		}

		public init(_ game: Game.Archived) {
			self.init(
				score: game.score,
				bowlerName: game.bowlerName,
				leagueName: game.leagueName,
				seriesDate: game.seriesDate
			)
		}

		public var body: some SwiftUI.View {
			HStack {
				Image(systemSymbol: .calendar)

				VStack(alignment: .leading, spacing: .smallSpacing) {
					VStack(alignment: .leading, spacing: .tinySpacing) {
						Text(Strings.Archive.List.Games.itemTitle(seriesDate.longFormat, String(score)))
							.bold()
						Text(Strings.Archive.List.Games.belongsTo(bowlerName, leagueName))
							.font(.caption)
					}
				}
			}
		}
	}
}

#if DEBUG
struct GameArchivedViewPreview: PreviewProvider {
	static var previews: some View {
		List {
			Game.ArchivedView(score: 120, bowlerName: "Joseph", leagueName: "Majors 23/24", seriesDate: Date())
		}
	}
}
#endif
