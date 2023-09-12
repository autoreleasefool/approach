import AssetsLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI

extension Gear {
	public struct View: SwiftUI.View {
		let name: String
		let ownerName: String?
		let kind: Gear.Kind

		public init(name: String, kind: Gear.Kind, ownerName: String?) {
			self.name = name
			self.kind = kind
			self.ownerName = ownerName
		}

		public init(_ gear: Gear.Summary) {
			self.init(name: gear.name, kind: gear.kind, ownerName: gear.ownerName)
		}

		public var body: some SwiftUI.View {
			HStack(alignment: .firstTextBaseline) {
				Image(systemSymbol: kind.systemSymbol)
				VStack(alignment: .leading) {
					Text(name)
					if let ownerName {
						Text(Strings.Gear.ownedBy(ownerName))
							.font(.caption2)
					}
				}
			}
		}
	}
}

extension Gear.Kind {
	public var systemSymbol: SFSymbol {
		switch self {
		case .bowlingBall: return .poweroutletTypeH
		case .shoes: return .shoeprintsFill
		case .towel: return .squareSplitBottomrightquarter
		case .other: return .questionmarkApp
		}
	}
}

#if DEBUG
struct GearViewPreview: PreviewProvider {
	static var previews: some View {
		List {
			Gear.View(name: "Blue", kind: .bowlingBall, ownerName: "Joseph")
			Gear.View(name: "Reebok", kind: .shoes, ownerName: nil)
			Gear.View(name: "Towel", kind: .towel, ownerName: nil)
			Gear.View(name: "Favourite Shirt", kind: .other, ownerName: "Sarah")
		}
	}
}
#endif
