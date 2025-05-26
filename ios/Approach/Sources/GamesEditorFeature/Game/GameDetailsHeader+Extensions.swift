import AssetsLibrary
import ComposableArchitecture
import ModelsLibrary
import StringsLibrary

extension GameDetailsHeader {
	func startShimmering(ifEnabled shimmerEnabled: Bool) -> Effect<Action> {
		shimmerEnabled ? .run { send in
			for color in [
				Asset.Colors.Primary.light.swiftUIColor,
				Asset.Colors.Primary.light.swiftUIColor.opacity(0),
				Asset.Colors.Primary.light.swiftUIColor,
				Asset.Colors.Primary.light.swiftUIColor.opacity(0),
				nil,
			] {
				guard !Task.isCancelled else { return }
				await send(.internal(.setShimmerColor(color)), animation: .easeInOut(duration: 0.5))
				try await clock.sleep(for: .milliseconds(500))
			}
		}
		.cancellable(id: CancelID.shimmering, cancelInFlight: true) : .none
	}
}

extension GameDetailsHeader.State {
	public enum NextElement: Equatable, CustomStringConvertible {
		case bowler(name: String, id: Bowler.ID)
		case roll(rollIndex: Int)
		case frame(frameIndex: Int)
		case game(gameIndex: Int, bowler: Bowler.ID, game: Game.ID)

		public var description: String {
			switch self {
			case let .bowler(name, _):
				name
			case let .roll(rollIndex):
				Strings.Roll.title(rollIndex + 1)
			case let .frame(frameIndex):
				Strings.Frame.title(frameIndex + 1)
			case let .game(gameIndex, _, _):
				Strings.Game.titleWithOrdinal(gameIndex + 1)
			}
		}
	}
}

extension GameDetailsHeader.State {
	func shouldStartShimmering() -> Effect<GameDetailsHeader.Action> {
		.send(.internal(.startShimmering))
	}
}
