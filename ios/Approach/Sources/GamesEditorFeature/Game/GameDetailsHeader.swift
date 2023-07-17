import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import GamesRepositoryInterface
import ModelsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct GameDetailsHeader: Reducer {
	public struct State: Equatable {
		public let currentBowlerName: String
		public let currentLeagueName: String
		public let next: NextElement?

		init(game: Game.Edit, next: NextElement?) {
			self.currentBowlerName = game.bowler.name
			self.currentLeagueName = game.league.name
			self.next = next
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapNext(State.NextElement)
		}
		public enum InternalAction: Equatable {}
		public enum DelegateAction: Equatable {
			case didProceed(to: State.NextElement)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public var body: some ReducerOf<Self> {
		Reduce { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didTapNext(next):
					return .send(.delegate(.didProceed(to: next)))
				}

			case let .internal(internalAction):
				switch internalAction {
				case .never:
					return .none
				}

			case .delegate:
				return .none
			}
		}
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
				return name
			case let .roll(rollIndex):
				return Strings.Roll.title(rollIndex + 1)
			case let .frame(frameIndex):
				return Strings.Frame.title(frameIndex + 1)
			case let .game(gameIndex, _, _):
				return Strings.Game.titleWithOrdinal(gameIndex + 1)
			}
		}
	}
}

// MARK: - View

public struct GameDetailsHeaderView: View {
	let store: StoreOf<GameDetailsHeader>

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			HStack(alignment: .top) {
				VStack(alignment: .leading) {
					Text(viewStore.currentBowlerName)
						.font(.headline)
						.frame(maxWidth: .infinity, alignment: .leading)
					Text(viewStore.currentLeagueName)
						.font(.subheadline)
						.frame(maxWidth: .infinity, alignment: .leading)
				}

				Spacer()

				if let next = viewStore.next {
					Button { viewStore.send(.didTapNext(next)) } label: {
						HStack {
							Text(String(describing: next))
								.font(.caption)
							Image(systemName: "chevron.forward")
								.resizable()
								.scaledToFit()
								.frame(width: .tinyIcon, height: .tinyIcon)
						}
					}
					.contentShape(Rectangle())
					.buttonStyle(TappableElement())
				}
			}
			.listRowInsets(EdgeInsets())
			.listRowBackground(Color.clear)
		})
	}
}
