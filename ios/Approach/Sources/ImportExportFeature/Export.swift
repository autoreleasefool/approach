import AssetsLibrary
import ComposableArchitecture
import DateTimeLibrary
import FeatureActionLibrary
import Foundation
import ImportExportServiceInterface
import PreferenceServiceInterface
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct Export: Reducer {
	public struct State: Equatable {
		public var exportUrl: URL?
		public var lastExportAt: Date
		public var errorMessage: String? = "Error"

		public init() {
			@Dependency(\.preferences) var preferences
			self.lastExportAt = Date(timeIntervalSince1970: preferences.double(forKey: .dataLastExportDate) ?? 0)
		}

		public var shareUrl: URL { exportUrl ?? URL(string: "https://tryapproach.app")! }
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didFirstAppear
			case didTapRetryButton
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case didReceiveEvent(TaskResult<ExportService.Event>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public init() {}

	@Dependency(\.date) var  date
	@Dependency(\.export) var export
	@Dependency(\.preferences) var preferences

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didFirstAppear:
					return .merge(
						fetchExportData(&state),
						.run { _ in preferences.setKey(.dataLastExportDate, toDouble: date().timeIntervalSince1970)}
					)

				case .didTapRetryButton:
					return fetchExportData(&state)
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didReceiveEvent(.success(event)):
					switch event {
					case .progress:
						return .none
					case let .response(url):
						state.exportUrl = url
						return .none
					}

				case let .didReceiveEvent(.failure(error)):
					state.exportUrl = nil
					state.errorMessage = error.localizedDescription
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}

	private func fetchExportData(_ state: inout State) -> Effect<Action> {
		state.errorMessage = nil
		return .run { send in
			for try await event in export.exportDatabase() {
				await send(.internal(.didReceiveEvent(.success(event))))
			}
		} catch: { error, send in
			await send(.internal(.didReceiveEvent(.failure(error))))
		}
	}
}

// MARK: - View

public struct ExportView: View {
	let store: StoreOf<Export>

	public init(store: StoreOf<Export>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			VStack(spacing: 0) {
				List {
					Section {
						Text(Strings.Export.exportAnytime)
					}

					Section {
						Text(Strings.Export.weRecommend)
					} footer: {
						if viewStore.lastExportAt.timeIntervalSince1970 == .zero {
							Text(Strings.Export.neverExported)
						} else {
							Text(Strings.Export.lastExportedAt(viewStore.lastExportAt.longFormat))
						}
					}

					Section {
						Text(Strings.Export.yourData)
					}
				}

				Divider()

				exportButton(viewStore)
				errorView(viewStore)
			}
			.navigationTitle(Strings.Export.title)
			.onFirstAppear { viewStore.send(.didFirstAppear) }
		})
	}

	@ViewBuilder @MainActor private func exportButton(
		_ viewStore: ViewStore<Export.State, Export.Action.ViewAction>
	) -> some View {
		ShareLink(item: viewStore.shareUrl) {
			Text(Strings.Export.exportData)
				.frame(maxWidth: .infinity)
		}
		.disabled(viewStore.exportUrl == nil)
		.modifier(PrimaryButton())
		.padding()
	}

	@ViewBuilder @MainActor private func errorView(
		_ viewStore: ViewStore<Export.State, Export.Action.ViewAction>
	) -> some View {
		if let error = viewStore.errorMessage {
			VStack {
				Text(Strings.Export.errorMessage(error))
					.foregroundColor(Asset.Colors.Error.default)

				Button(Strings.Action.tryAgain) {
					viewStore.send(.didTapRetryButton)
				}
				.buttonStyle(.borderless)
			}
			.padding(.horizontal)
			.padding(.bottom)
		}
	}
}

#if DEBUG
struct ExportPreview: PreviewProvider {
	static var previews: some View {
		NavigationStack {
			ExportView(store: .init(
				initialState: Export.State(),
				reducer: Export.init
			))
		}
	}
}
#endif
