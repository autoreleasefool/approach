import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import GamesRepositoryInterface
import ModelsLibrary
import SwiftUI
import ViewsLibrary

public struct GameDetailsHeader: Reducer {
	public struct State: Equatable {
		public let currentBowlerName: String
		public let currentLeagueName: String
		public let nextElement: String?

		init(game: Game.Edit, nextElement: String?) {
			self.currentBowlerName = game.bowler.name
			self.currentLeagueName = game.league.name
			self.nextElement = nextElement
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {}
		public enum InternalAction: Equatable {}
		public enum DelegateAction: Equatable {
			case didProceedToNextElement
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public var body: some Reducer<State, Action> {
		Reduce { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .never:
					return .none
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

// MARK: - View

public struct GameDetailsHeaderView: View {
	let store: StoreOf<GameDetailsHeader>

	enum ViewAction {
		case didTapNext
	}

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: GameDetailsHeader.Action.init, content: { viewStore in
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

				if let next = viewStore.nextElement {
					Button { viewStore.send(.didTapNext) } label: {
						HStack {
							Text(next)
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

extension GameDetailsHeader.Action {
	init(action: GameDetailsHeaderView.ViewAction) {
		switch action {
		case .didTapNext:
			self = .delegate(.didProceedToNextElement)
		}
	}
}
