import AssetsLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI

extension Bowler {
	public struct View: SwiftUI.View {
		let name: String
		let kind: Bowler.Kind?

		public init(name: String, kind: Bowler.Kind?) {
			self.name = name
			self.kind = kind
		}

		public init(_ bowler: Bowler.Summary) {
			self.init(name: bowler.name, kind: nil)
		}

		public init(_ bowler: Bowler.Opponent) {
			self.init(name: bowler.name, kind: bowler.kind)
		}

		public var body: some SwiftUI.View {
			HStack(alignment: .center) {
				Text(name)

				Spacer()

				if let symbol = kind?.systemSymbol {
					Image(systemSymbol: symbol)
				}
			}
		}
	}
}

extension Bowler.Kind {
	public var systemSymbol: SFSymbol? {
		switch self {
		case .opponent: return nil
		case .playable: return .personFill
		}
	}
}

#if DEBUG
#Preview {
	List {
		Bowler.View(name: "Joseph", kind: .opponent)
		Bowler.View(name: "Joseph", kind: .playable)
		Bowler.View(name: "Joseph", kind: nil)
	}
}
#endif
