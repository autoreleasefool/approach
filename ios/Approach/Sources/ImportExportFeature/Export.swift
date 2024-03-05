import AnalyticsServiceInterface
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

@Reducer
public struct Export: Reducer {
	@ObservableState
	public struct State: Equatable {
		public var exportUrl: URL?
		public var lastExportAt: Date
		public var errorMessage: String? = "Error"

		public init() {
			@Dependency(PreferenceService.self) var preferences
			self.lastExportAt = Date(timeIntervalSince1970: preferences.double(forKey: .dataLastExportDate) ?? 0)
		}

		public var shareUrl: URL { exportUrl ?? URL(string: "https://tryapproach.app")! }
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable public enum View {
			case onAppear
			case didFirstAppear
			case didTapRetryButton
		}
		@CasePathable public enum Delegate { case doNothing }
		@CasePathable public enum Internal {
			case didReceiveEvent(Result<ExportService.Event, Error>)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	public init() {}

	@Dependency(\.date) var  date
	@Dependency(ExportService.self) var export
	@Dependency(PreferenceService.self) var preferences

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

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

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}

		AnalyticsReducer<State, Action> { _, action in
			switch action {
			case .internal(.didReceiveEvent(.success)):
				return Analytics.Data.Exported()
			default:
				return nil
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

@ViewAction(for: Export.self)
public struct ExportView: View {
	public let store: StoreOf<Export>

	public init(store: StoreOf<Export>) {
		self.store = store
	}

	public var body: some View {
		WithPerceptionTracking {
			VStack(spacing: 0) {
				List {
					Section {
						Text(Strings.Export.exportAnytime)
					}

					Section {
						Text(Strings.Export.weRecommend)
					} footer: {
						if store.lastExportAt.timeIntervalSince1970 == .zero {
							Text(Strings.Export.neverExported)
						} else {
							Text(Strings.Export.lastExportedAt(store.lastExportAt.longFormat))
						}
					}

					Section {
						Text(Strings.Export.yourData)
					}
				}

				Divider()

				exportButton
				errorView
			}
			.navigationTitle(Strings.Export.title)
			.onFirstAppear { send(.didFirstAppear) }
			.onAppear { send(.onAppear) }
		}
	}

	private var exportButton: some View {
		ShareLink(item: store.shareUrl) {
			Text(Strings.Export.exportData)
				.frame(maxWidth: .infinity)
		}
		.disabled(store.exportUrl == nil)
		.modifier(PrimaryButton())
		.padding()
	}

	@ViewBuilder private var errorView: some View {
		if let error = store.errorMessage {
			VStack {
				Text(Strings.Export.errorMessage(error))
					.foregroundColor(Asset.Colors.Error.default)

				Button(Strings.Action.tryAgain) {
					send(.didTapRetryButton)
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
