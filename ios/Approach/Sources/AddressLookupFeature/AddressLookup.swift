import AddressLookupServiceInterface
import ComposableArchitecture
import FeatureActionLibrary
import Foundation
import LocationsRepositoryInterface
import ModelsLibrary
import StringsLibrary

public struct AddressLookup: Reducer {
	public struct State: Equatable {
		@BindingState public var query: String
		public var results: IdentifiedArrayOf<AddressLookupResult> = []

		public var isLoadingAddress = false
		public var loadingAddressError: String?
		public var loadingResultsError: String?

		public init(initialQuery: String) {
			self.query = initialQuery
		}
	}

	public enum Action: FeatureAction, BindableAction, Equatable {
		public enum ViewAction: Equatable {
			case didAppear
			case didDisappear
			case didTapCancelButton
			case didTapResult(AddressLookupResult.ID)
		}
		public enum DelegateAction: Equatable {
			case didSelectAddress(Location.Edit)
		}
		public enum InternalAction: Equatable {
			case didReceiveResults(TaskResult<[AddressLookupResult]>)
			case didLoadAddress(TaskResult<Never>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
		case binding(BindingAction<State>)
	}

	enum SearchID { case lookup }
	enum LookupError: Error {
		case addressNotFound
	}

	public init() {}

	@Dependency(\.addressLookupService) var addressLookup
	@Dependency(\.dismiss) var dismiss

	public var body: some Reducer<State, Action> {
		BindingReducer()

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didAppear:
					return .merge(
						.run { send in
							for try await results in await addressLookup.beginSearch(SearchID.lookup) {
								await send(.internal(.didReceiveResults(.success(results))))
							}
						} catch: { error, send in
							await send(.internal(.didReceiveResults(.failure(error))))
						}.cancellable(id: SearchID.lookup),
						.run { [query = state.query] _ in
							guard !query.isEmpty else { return }
							await addressLookup.updateSearchQuery(SearchID.lookup, query)
						}
					)

				case .didDisappear:
					return .merge(
						.cancel(id: SearchID.lookup),
						.run { _ in await addressLookup.finishSearch(SearchID.lookup) }
					)

				case .didTapCancelButton:
					return .merge(
						.cancel(id: SearchID.lookup),
						.run { _ in await self.dismiss() }
					)

				case let .didTapResult(id):
					guard let address = state.results[id: id] else { return .none }
					state.isLoadingAddress = true
					return .run { send in
						guard let location = try await addressLookup.lookUpAddress(address) else {
							return await send(.internal(.didLoadAddress(.failure(LookupError.addressNotFound))))
						}

						await send(.delegate(.didSelectAddress(.init(
							id: location.id,
							title: location.title,
							subtitle: location.subtitle,
							coordinate: location.coordinate
						))))
					} catch: { error, send in
						await send(.internal(.didLoadAddress(.failure(error))))
					}
				}

			case let .internal(internalAction):
				switch internalAction {

				case let .didReceiveResults(.success(results)):
					state.results = .init(uniqueElements: results)
					return .none

				case let .didReceiveResults(.failure(error)):
					state.loadingResultsError = error.localizedDescription
					return .none

				case let .didLoadAddress(.failure(error)):
					state.isLoadingAddress = false
					state.loadingAddressError = error.localizedDescription
					return .none
				}

			case let .delegate(delegateAction):
				switch delegateAction {
				case .didSelectAddress:
					return .merge(
						.cancel(id: SearchID.lookup),
						.run { _ in await self.dismiss() }
					)
				}

			case .binding(\.$query):
				state.loadingAddressError = nil
				state.loadingResultsError = nil
				return .run { [query = state.query] _ in
					await addressLookup.updateSearchQuery(SearchID.lookup, query)
				}

			case .binding:
				return .none
			}
		}
	}
}

extension AddressLookup.LookupError: LocalizedError {
	public var errorDescription: String? {
		switch self {
		case .addressNotFound: return Strings.Address.Error.notFound
		}
	}
}
