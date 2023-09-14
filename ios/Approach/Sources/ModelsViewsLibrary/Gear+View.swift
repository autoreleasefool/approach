import AssetsLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI

extension Gear {
	public struct View: SwiftUI.View {
		let name: String
		let ownerName: String?
		let kind: Gear.Kind
		let avatar: Avatar.Value

		public init(name: String, kind: Gear.Kind, ownerName: String?, avatar: Avatar.Value) {
			self.name = name
			self.kind = kind
			self.ownerName = ownerName
			self.avatar = avatar
		}

		public init(_ gear: Gear.Summary) {
			self.init(name: gear.name, kind: gear.kind, ownerName: gear.ownerName, avatar: gear.avatar.value)
		}

		public var body: some SwiftUI.View {
			HStack(alignment: .center) {
				VStack(alignment: .leading) {
					Text(name)
					if let ownerName {
						Text(Strings.Gear.ownedBy(ownerName))
							.font(.caption2)
					}
				}

				Spacer()

				Image(systemSymbol: kind.systemSymbol)
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
			Gear.View(name: "Blue", kind: .bowlingBall, ownerName: "Joseph", avatar: .text("", .default))
			Gear.View(name: "Reebok", kind: .shoes, ownerName: nil, avatar: .text("", .default))
			Gear.View(name: "Towel", kind: .towel, ownerName: nil, avatar: .text("", .default))
			Gear.View(name: "Favourite Shirt", kind: .other, ownerName: "Sarah", avatar: .text("", .default))
		}
	}
}
#endif
